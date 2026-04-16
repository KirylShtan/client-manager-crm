package clientapp.natadataservicemanagement.service;
import clientapp.natadataservicemanagement.dto.DtoActualClient;
import clientapp.natadataservicemanagement.exception.ClientNotFoundException;
import clientapp.natadataservicemanagement.model.*;
import clientapp.natadataservicemanagement.repository.ActualClientRepository;
import clientapp.natadataservicemanagement.repository.CompletedClientRepository;
import clientapp.natadataservicemanagement.repository.TelegramSubscriberRepository;
import jakarta.transaction.Transactional;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;



@Service
public class ActualClientService extends BasicClientServiceImpl<ActualClient> {
    private static final Logger logger = LoggerFactory.getLogger(ActualClientService.class);

   private final CompletedClientRepository completedClientRepository;
   private final VaultService vaultService;
   private final TelegramSubscriberRepository telegramSubscriberRepository;
   private final ActualClientRepository actualClientRepository;

   @Autowired
    public ActualClientService(ActualClientRepository actualClientRepository, CompletedClientRepository completedClientRepository,
                               VaultService vaultService, TelegramSubscriberRepository telegramSubscriberRepository) {
        super(actualClientRepository);
        this.completedClientRepository = completedClientRepository;
        this.vaultService = vaultService;
        this.telegramSubscriberRepository = telegramSubscriberRepository;
        this.actualClientRepository = actualClientRepository;
    }

    @Transactional
    public void archiveClient(Long clientId) {
        logger.info("Archiving client with id={}", clientId);

        ActualClient actualClient = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id=" + clientId));
        logger.info("Changing status");
        actualClient.setStatus("Finished");
        logger.debug("Archiving client id={} : copying data and saving to completed repository", clientId);
        CompletedClient completedClient = new CompletedClient();
        copyClientData(actualClient, completedClient);
        completedClientRepository.save(completedClient);
        telegramSubscriberRepository.deleteByClientId(clientId);
        clientRepository.deleteById(clientId);
        logger.info("Client id={} successfully archived to completed repository and removed from ActualClients", clientId);
    }

    private void copyClientData(ActualClient source, CompletedClient target) {
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setCaseNumber(source.getCaseNumber());
        target.setSubmissionDate(source.getSubmissionDate());
        target.setStatus(source.getStatus());
        target.setCompanyName(source.getCompanyName());
        target.setArchiveDate(LocalDate.now());
        target.setNote(source.getNote());
        target.setEmail(source.getEmail());
        target.setPayed(source.getPayed());

    }
    @Transactional
    public ActualClient addActualClientFromDto(DtoActualClient dto) {
       if (dto.getRealPassword() == null || dto.getRealPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty for caseNumber=" + dto.getCaseNumber());
        }


        ActualClient client = new ActualClient();
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setCaseNumber(dto.getCaseNumber());
        client.setSubmissionDate(dto.getSubmissionDate());
        client.setStatus(dto.getStatus());
        client.setCompanyName(dto.getCompanyName());
        client.setEmail(dto.getEmail());


        client = clientRepository.save(client);


        String vaultKey = "client-" + client.getId();
        client.setVaultKey(vaultKey);
        client = clientRepository.save(client);
        vaultService.saveClientPassword(vaultKey, dto.getRealPassword());
        logger.info("New client added with caseNumber={} and vaultKey={}", dto.getCaseNumber(), vaultKey);

        return client;
    }
    public String openStatusPageWithCredentials(DtoActualClient dto) {
        String password = vaultService.getClientPassword(dto.getVaultKey());
        String caseNumber = dto.getCaseNumber();
        if (caseNumber == null || caseNumber.isBlank()) {
            throw new IllegalArgumentException("Case number is missing");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalStateException("There is no password associated with this case");
        }
        String actionUrl = "https://www.poznan.uw.gov.pl/cudzoziemcy-stan/?lang=pl";
        return """
        <!doctype html>
        <html>
          <head>
            <meta charset="UTF-8">
            <title>Open Case Status</title>
          </head>
          <body>
            <h3>Continue to government status page</h3>
            <p>Verify values, solve captcha, then click submit.</p>
            <form id="govForm" method="post" action="%s">
              <label>Numer sprawy</label><br/>
              <input type="text" name="nr_sprawy" value="%s" /><br/><br/>
              <label>Kod</label><br/>
              <input type="text" name="kod" value="%s" /><br/><br/>
              <button type="submit">Open status page</button>
            </form>
          </body>
        </html>
        """.formatted(
                escapeHtml(actionUrl),
                escapeHtml(caseNumber),
                escapeHtml(password)
        );
    }
    public DtoActualClient getDtoById(Long id) {
        ActualClient client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found: " + id));

        DtoActualClient dto = new DtoActualClient();
        dto.setId(client.getId());
        dto.setCaseNumber(client.getCaseNumber());
        dto.setVaultKey(client.getVaultKey());
        return dto;
    }
    public List<ActualClient> findClientsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return actualClientRepository.findBySubmissionDateBetween(startDate, endDate);
    }

    public ActualClient getClientNoteOrThrow(Long id) {
       return actualClientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException("Client with id " + id + " not found"));
    }
    @Transactional
    public void deleteWithDependencies(Long id) {
       ActualClient client = actualClientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException("Client with id " + id + " not found"));
       telegramSubscriberRepository.deleteByClientId(client.getId());
       actualClientRepository.deleteById(id);
       logger.info("Client id={} and related TelegramSubscriber successfully deleted", id);
   }

    public List<ActualClient> getAllClientsOrThrow() {
        List<ActualClient> clients = actualClientRepository.findAll();
        if (clients.isEmpty()) {
            logger.warn("Repository is empty");
        }
        clients.stream().limit(5)
                .forEach(c -> logger.debug("Client: caseNumber={}, status={}", c.getCaseNumber(), c.getStatus()));
        return clients;
    }

    public List<ActualClient> searchClients(
            Long id,
            String firstName,
            String lastName,
            String caseNumber,
            String submissionDate,
            String status,
            LocalDate archiveDate,
            String companyName,
            String payed,
            Boolean result
    ) {
        List<ActualClient> allClients = getAllClientsOrThrow();
        return filterClients(allClients, id, firstName, lastName, caseNumber,
                submissionDate, status, archiveDate, companyName, payed, result);
    }
    private String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}





