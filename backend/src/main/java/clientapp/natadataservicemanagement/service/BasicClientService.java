package clientapp.natadataservicemanagement.service;

import clientapp.natadataservicemanagement.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BasicClientService<T> {

    public List<T> getAllClients();
    public T updateClient(Long id,T updatedClient);
    public void deleteClient(Long id);
    public <T extends Client> List<T> filterClients(
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
    );
    public Page<T> getAllClientsPaginated(Pageable pageable);

    public <T extends Client> List<T> fetchAllClients(JpaRepository<T,Long> repository);
    public <T> Page<T> fetchAllClients(Pageable pageable, JpaRepository<T, Long> repository);
    public T updateClientNote(Long id, String note);
    public T getClientNote(Long id);

}
