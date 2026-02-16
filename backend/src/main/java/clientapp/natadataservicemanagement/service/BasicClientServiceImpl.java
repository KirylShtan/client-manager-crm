package clientapp.natadataservicemanagement.service;

import clientapp.natadataservicemanagement.exception.ClientNotFoundException;
import clientapp.natadataservicemanagement.model.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BasicClientServiceImpl<T extends Client> implements  BasicClientService<T> {

    protected final JpaRepository<T,Long> clientRepository;

    private static final Logger logger = LoggerFactory.getLogger(BasicClientServiceImpl.class);

    protected BasicClientServiceImpl(JpaRepository<T,Long> clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public List<T> getAllClients() {
        logger.info("Getting list of all actual clients");
        return clientRepository.findAll();
    }
    @Override
    public Page<T> getAllClientsPaginated(Pageable pageable) {
        logger.info("Preparing page");
        return clientRepository.findAll(pageable);
    }
    @Override
    public void deleteClient(Long id){
        logger.info("Deleting client");
        clientRepository.deleteById(id);


    }
    @Override
    public List<T> fetchAllClients(JpaRepository<T,Long> clientRepository){
        logger.info("fetching all clients");
       return clientRepository.findAll();
    }
    @Override
    public Page<T> fetchAllClients(Pageable pageable, JpaRepository<T,Long> clientRepository){
        logger.info("Preparing fetched page");
        return clientRepository.findAll(pageable);
    }
    @Override
    public T updateClient(Long id, T client){
        logger.info("Updating client with id={} and caseNumber={}", id, client.getCaseNumber());
        T existingClient = clientRepository.findById(id).orElseThrow(() ->
                new ClientNotFoundException("Client didn't found with id= " + id));
        existingClient.setFirstName(client.getFirstName());
        existingClient.setLastName(client.getLastName());
        existingClient.setCaseNumber(client.getCaseNumber());
        existingClient.setSubmissionDate(client.getSubmissionDate());
        existingClient.setStatus(client.getStatus());
        existingClient.setCompanyName(client.getCompanyName());
        existingClient.setPayed(client.getPayed());
        T saved =  clientRepository.save(existingClient);
        logger.debug("Updated client details: id={}, caseNumber={}, status={}",
                saved.getId(), saved.getCaseNumber(), saved.getStatus());
        return saved;
    }
    @Override
    public List<T> filterClients(
            List<T> clients,
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
    ){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return clients.stream()

                .filter(c -> id == null || c.getId().equals(id))
                .filter(c -> {
                    boolean matches = false;

                    if (firstName != null && !firstName.isBlank() && c.getFirstName() != null){
                        matches |= c.getFirstName().toLowerCase().contains(firstName.toLowerCase());
                    }

                    if (lastName != null && !lastName.isBlank() && c.getLastName() != null){
                        matches |= c.getLastName().toLowerCase().contains(lastName.toLowerCase());
                    }

                    if (caseNumber != null && !caseNumber.isBlank() && c.getCaseNumber() != null){
                        matches |= c.getCaseNumber().toLowerCase().contains(caseNumber.toLowerCase());
                    }

                    if (status != null && !status.isBlank() && c.getStatus() != null){
                        matches |= c.getStatus().toLowerCase().contains(status.toLowerCase());
                    }

                    if (companyName != null && !companyName.isBlank() && c.getCompanyName() != null){
                        matches |= c.getCompanyName().toLowerCase().contains(companyName.toLowerCase());
                    }

                    if (submissionDate != null && !submissionDate.isBlank() && c.getSubmissionDate() != null) {
                        LocalDate inputDate = LocalDate.parse(submissionDate, formatter);
                        matches |= c.getSubmissionDate().equals(inputDate);
                    }


                    if ((firstName == null || firstName.isBlank()) &&
                            (lastName == null || lastName.isBlank()) &&
                            (caseNumber == null || caseNumber.isBlank()) &&
                            (status == null || status.isBlank()) &&
                            (companyName == null || companyName.isBlank()) &&
                            (submissionDate == null || submissionDate.isBlank()))
                    {
                        matches = true;
                    }

                    return matches;
                })
                .peek(c -> logger.debug("Client: id={}, caseNumber={}, status={}", c.getId(), c.getCaseNumber(), c.getStatus()))
                .collect(Collectors.toList());
    }
    @Override
    public T getClientNote(Long id){
        logger.info("Getting client note");
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));
    }
    @Override
    public T updateClientNote(Long id,String note){
        logger.info("updating client note");
        T client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));
        client.setNote(note);
        return clientRepository.save(client);

    }
    @Override
    public List<T> findBetweenDates(List<T> clients, String startDate, String endDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate start = LocalDate.parse(startDate,formatter);
        LocalDate end  = LocalDate.parse(endDate,formatter);
        logger.info("Getting list of clients between dates");
        return clients.stream()
                .filter(c -> c.getSubmissionDate() != null)
                .filter(c -> !c.getSubmissionDate().isBefore(start) && !c.getSubmissionDate().isAfter(end))
                .collect(Collectors.toList());
    }
}

