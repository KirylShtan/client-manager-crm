package clientapp.natadataservicemanagement.repository;


import clientapp.natadataservicemanagement.model.NegativeClient;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface NegativeClientRepository extends JpaRepository<NegativeClient,Long> {
    List<NegativeClient> findByLastName(String lastName);
    List<NegativeClient> findBySubmissionDate(LocalDate date);
    List<NegativeClient> findByFirstName(String name);
    List<NegativeClient> findByStatus(String status);
    Page<NegativeClient> findAll(Pageable pageable);
}
