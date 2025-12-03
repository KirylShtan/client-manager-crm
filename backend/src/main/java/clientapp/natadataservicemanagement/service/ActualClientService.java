package clientapp.natadataservicemanagement.service;
import clientapp.natadataservicemanagement.dto.DtoActualClient;
import clientapp.natadataservicemanagement.exception.ClientNotFoundException;
import clientapp.natadataservicemanagement.model.*;
import clientapp.natadataservicemanagement.repository.ActualClientRepository;
import clientapp.natadataservicemanagement.repository.CompletedClientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@Service
public class ActualClientService extends BasicClientServiceImpl<ActualClient> {
    private static final Logger logger = LoggerFactory.getLogger(ActualClientService.class);

   private final CompletedClientRepository completedClientRepository;

   @Autowired
    public ActualClientService(ActualClientRepository actualClientRepository, CompletedClientRepository completedClientRepository) {
        super(actualClientRepository);
        this.completedClientRepository = completedClientRepository;
    }

    @Transactional
    public void archiveClient(Long clientId) {
        logger.info("Archiving client with id={}", clientId);

        ActualClient actualClient = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Client not found with id=" + clientId));

        actualClient.setStatus("Finished");

        CompletedClient completedClient = new CompletedClient();
        copyClientData(actualClient, completedClient);

        completedClientRepository.save(completedClient);
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
        target.setPayed(source.getPayed());
    }

    public ActualClient addedActualClientFromDto(DtoActualClient dto){
        ActualClient client = new ActualClient();
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setCaseNumber(dto.getCaseNumber());
        client.setSubmissionDate(dto.getSubmissionDate());
        client.setStatus(dto.getStatus());
        client.setCompanyName(dto.getCompanyName());
        logger.info("Adding new client with caseNumber={}, status={}", dto.getCaseNumber(), dto.getStatus());
        return clientRepository.save(client);
    }





















}