package clientapp.natadataservicemanagement.repository;


import clientapp.natadataservicemanagement.model.PositiveClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PositiveClientRepository extends JpaRepository<PositiveClient,Long> {
    List<PositiveClient> findByLastName(String lastName);
    List<PositiveClient> findBySubmissionDate(LocalDate date);
    List<PositiveClient> findByFirstName(String name);
    List<PositiveClient> findByStatus(String status);
    Page<PositiveClient> findAll(Pageable pageable);
}
