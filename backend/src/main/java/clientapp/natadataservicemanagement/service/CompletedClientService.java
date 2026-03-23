package clientapp.natadataservicemanagement.service;

import clientapp.natadataservicemanagement.exception.ClientNotFoundException;
import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.model.CompletedClient;
import clientapp.natadataservicemanagement.repository.CompletedClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CompletedClientService extends BasicClientServiceImpl<CompletedClient> {

    private final CompletedClientRepository completedClientRepository;

    private static final Logger logger = LoggerFactory.getLogger(CompletedClientService.class);
    @Autowired
    public CompletedClientService(CompletedClientRepository completedClientRepository) {
        super(completedClientRepository);
        this.completedClientRepository = completedClientRepository;

    }
    public List<CompletedClient> searchClients(
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
        List<CompletedClient> allClients = getAllClientsOrThrow();
        return filterClients(allClients, id, firstName, lastName, caseNumber,
                submissionDate, status, archiveDate, companyName, payed, result);
    }
    public List<CompletedClient> getAllClientsOrThrow() {
        List<CompletedClient> clients = completedClientRepository.findAll();
        if (clients.isEmpty()) {
            throw new ClientNotFoundException("No completed clients found");
        }
        clients.stream().limit(5)
                .forEach(c -> logger.debug("Client: caseNumber={}, status={}", c.getCaseNumber(), c.getStatus()));
        return clients;
    }





}
