package clientapp.natadataservicemanagement.service;

import clientapp.natadataservicemanagement.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BasicClientService<T extends Client> {

     List<T> getAllClients();
     T updateClient(Long id,T updatedClient);
     void deleteClient(Long id);
     List<T> filterClients(
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
            Boolean result);

     Page<T> getAllClientsPaginated(Pageable pageable);

     List<T> fetchAllClients(JpaRepository<T,Long> repository);
     Page<T> fetchAllClients(Pageable pageable, JpaRepository<T, Long> repository);
     T updateClientNote(Long id, String note);
     T getClientNote(Long id);
     List<T> findBetweenDates(List<T> clients, String startDate, String endDate);

}
