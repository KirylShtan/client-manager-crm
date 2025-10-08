package clientapp.natadataservicemanagement.service;
import clientapp.natadataservicemanagement.dto.DtoActualClient;
import clientapp.natadataservicemanagement.exception.ClientNotFoundException;
import clientapp.natadataservicemanagement.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.model.NegativeClient;
import clientapp.natadataservicemanagement.model.PositiveClient;
import clientapp.natadataservicemanagement.repository.ClientRepository;
import clientapp.natadataservicemanagement.repository.NegativeClientRepository;
import clientapp.natadataservicemanagement.repository.PositiveClientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ClientService {
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private NegativeClientRepository negativeClientRepository;

    @Autowired
    private PositiveClientRepository positiveClientRepository;



    public List<ActualClient> getAllClients() {
        return fetchAllClients(clientRepository);
    }
    public ActualClient updateActualClient(Long id, ActualClient updatedClient) {
        logger.info("Updating client with id={} and caseNumber={}", id, updatedClient.getCaseNumber());
        ActualClient existingClient = clientRepository.findById(id).orElseThrow(() ->
                new ClientNotFoundException("Client didn't found with id= " + id));
        existingClient.setFirstName(updatedClient.getFirstName());
        existingClient.setLastName(updatedClient.getLastName());
        existingClient.setCaseNumber(updatedClient.getCaseNumber());
        existingClient.setSubmissionDate(updatedClient.getSubmissionDate());
        existingClient.setStatus(updatedClient.getStatus());
        ActualClient saved = clientRepository.save(existingClient);

        logger.debug("Updated client details: id={}, caseNumber={}, status={}",
                saved.getId(), saved.getCaseNumber(), saved.getStatus());
        return saved;
    }


    public NegativeClient updateNegativeClient(Long id, NegativeClient updatedClient) {
        logger.info("Updating client with id={} and caseNumber={}", id, updatedClient.getCaseNumber());
        NegativeClient existingNegativeClient = negativeClientRepository.findById(id).orElseThrow(() ->
                new ClientNotFoundException("Client didn't found with id= " + id));
        existingNegativeClient.setFirstName(updatedClient.getFirstName());
        existingNegativeClient.setLastName(updatedClient.getLastName());
        existingNegativeClient.setCaseNumber(updatedClient.getCaseNumber());
        existingNegativeClient.setSubmissionDate(updatedClient.getSubmissionDate());
        existingNegativeClient.setStatus(updatedClient.getStatus());

        NegativeClient saved = negativeClientRepository.save(existingNegativeClient);
        logger.debug("Updated client details: id={}, caseNumber={}, status={}",
                saved.getId(), saved.getCaseNumber(), saved.getStatus());

        return saved ;
    }

    public PositiveClient updatePositiveClient(Long id, PositiveClient updatedClient) {
        logger.info("Updating client with id={} and caseNumber={}", id, updatedClient.getCaseNumber());
        PositiveClient existingPositiveClient = positiveClientRepository.findById(id).orElseThrow(() ->
                new ClientNotFoundException("Client didn't found with id= " + id));
        existingPositiveClient.setFirstName(updatedClient.getFirstName());
        existingPositiveClient.setLastName(updatedClient.getLastName());
        existingPositiveClient.setCaseNumber(updatedClient.getCaseNumber());
        existingPositiveClient.setSubmissionDate(updatedClient.getSubmissionDate());
        existingPositiveClient.setStatus(updatedClient.getStatus());
        PositiveClient saved = positiveClientRepository.save(existingPositiveClient);
        logger.debug("Updated client details: id={}, caseNumber={}, status={}",
                saved.getId(), saved.getCaseNumber(), saved.getStatus());
        return saved;
    }


    @Transactional
    public void archiveClient(Long clientId, boolean isPositive) {
        logger.info("Archiving client with id={} as {}", clientId, isPositive ? "positive" : "negative");
        ActualClient actualClient = clientRepository.findById(clientId).orElseThrow(() ->
                new ClientNotFoundException("Client didn't found with id= " + clientId));

        actualClient.setStatus("Finished");
        clientRepository.save(actualClient);

        if (isPositive) {
            PositiveClient positiveClient = new PositiveClient();
            copyClientData(actualClient, positiveClient);
            positiveClient.setArchiveDate(LocalDate.now());
            positiveClientRepository.save(positiveClient);

        } else if (!isPositive) {
            NegativeClient negativeClient = new NegativeClient();
            copyClientData(actualClient, negativeClient);
            negativeClient.setArchiveDate(LocalDate.now());
            negativeClientRepository.save(negativeClient);
        }
        clientRepository.flush();
        clientRepository.deleteById(clientId);
        logger.info("Client id={} successfully archived to {} repository and removed from ActualClients",
                clientId, isPositive ? "PositiveClients" : "NegativeClients");
    }


    private void copyClientData(ActualClient source, Object target) {
        if (target instanceof PositiveClient) {
            PositiveClient positiveClient = (PositiveClient) target;
            positiveClient.setFirstName(source.getFirstName());
            positiveClient.setLastName(source.getLastName());
            positiveClient.setCaseNumber(source.getCaseNumber());
            positiveClient.setSubmissionDate(source.getSubmissionDate());
            positiveClient.setStatus(source.getStatus());
        } else if (target instanceof NegativeClient) {
            NegativeClient negativeClient = (NegativeClient) target;
            negativeClient.setFirstName(source.getFirstName());
            negativeClient.setLastName(source.getLastName());
            negativeClient.setCaseNumber(source.getCaseNumber());
            negativeClient.setSubmissionDate(source.getSubmissionDate());
            negativeClient.setStatus(source.getStatus());

        }
    }


    public void deleteClient(Long id) {

        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            logger.info("Client id={} successfully deleted ", id);
        } else {
            logger.warn("Attempted to delete client id={} but it was not found", id);
            throw new ClientNotFoundException("Client didn't found with id= " + id);
        }

    }


    public List<NegativeClient> getAllNegativeClients() {
        return fetchAllClients(negativeClientRepository);
    }


    public void deleteNegativeClient(Long id) {

        if (negativeClientRepository.existsById(id)) {
            negativeClientRepository.deleteById(id);
            logger.info("Client id={} successfully deleted ", id);
        } else {
            logger.warn("Attempted to delete client id={} but it was not found", id);
            throw new ClientNotFoundException("Клиент не найден!");
        }
    }


    public List<PositiveClient> getAllPositiveClients() {
        return fetchAllClients(positiveClientRepository);
    }


    public void deletePositiveClient(Long id) {
        if (positiveClientRepository.existsById(id)) {
            positiveClientRepository.deleteById(id);
            logger.info("Client id={} successfully deleted ", id);
        } else {
            logger.warn("Attempted to delete client id={} but it was not found", id);
            throw new ClientNotFoundException("Клиент не найден!");
        }

    }
    public <T extends Client> List<T> filterClients(
            List<T> clients,
            Long id,
            String firstName,
            String lastName,
            String caseNumber,
            LocalDate submissionDate,
            String status,
            LocalDate archiveDate) {

        return clients.stream()

                .filter(c -> id == null || c.getId().equals(id))
                .filter(c -> submissionDate == null || submissionDate.equals(c.getSubmissionDate()))
                .filter(c -> archiveDate == null || Objects.equals(archiveDate, c.getArchiveDate()))
                .filter(c -> {
                    boolean matches = false;

                    if (firstName != null && !firstName.isBlank() && c.getFirstName() != null)
                        matches |= c.getFirstName().toLowerCase().contains(firstName.toLowerCase());

                    if (lastName != null && !lastName.isBlank() && c.getLastName() != null)
                        matches |= c.getLastName().toLowerCase().contains(lastName.toLowerCase());

                    if (caseNumber != null && !caseNumber.isBlank() && c.getCaseNumber() != null)
                        matches |= c.getCaseNumber().toLowerCase().contains(caseNumber.toLowerCase());

                    if (status != null && !status.isBlank() && c.getStatus() != null)
                        matches |= c.getStatus().toLowerCase().contains(status.toLowerCase());


                    if ((firstName == null || firstName.isBlank()) &&
                            (lastName == null || lastName.isBlank()) &&
                            (caseNumber == null || caseNumber.isBlank()) &&
                            (status == null || status.isBlank())) {
                        matches = true;
                    }

                    return matches;
                })
                .peek(c -> logger.debug("Client: id={}, caseNumber={}, status={}", c.getId(), c.getCaseNumber(), c.getStatus()))
                .collect(Collectors.toList());
    }
    public Page<NegativeClient> getAllNegativeClientsPaginated(Pageable pageable) {
        return fetchAllClients(pageable,negativeClientRepository);
    }
    public Page<ActualClient> getAllActualClientsPaginated(Pageable pageable) {
        return fetchAllClients(pageable,clientRepository);
    }
    public Page<PositiveClient> getAllPositiveClientsPaginated(Pageable pageable) {
        return fetchAllClients(pageable,positiveClientRepository);
    }
    public ActualClient addedActualClientFromDto(DtoActualClient dto){
        ActualClient client = new ActualClient();
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setCaseNumber(dto.getCaseNumber());
        client.setSubmissionDate(dto.getSubmissionDate());
        client.setStatus(dto.getStatus());
        logger.info("Adding new client with caseNumber={}, status={}", dto.getCaseNumber(), dto.getStatus());
        return clientRepository.save(client);
    }
    private  <T extends Client> List<T> fetchAllClients(JpaRepository<T,Long> repository){
        List<T> clients = repository.findAll();
        clients.forEach(c -> logger.debug("Client : caseNumber = {}, status = {}", c.getCaseNumber(), c.getStatus()));
        return clients;
    }
    private <T> Page<T> fetchAllClients(Pageable pageable, JpaRepository<T, Long> repository) {
        return repository.findAll(pageable);
    }


}



