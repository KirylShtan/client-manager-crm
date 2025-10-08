package clientapp.natadataservicemanagement.repository;

import clientapp.natadataservicemanagement.model.ActualClient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

    @Repository
    public interface ClientRepository extends JpaRepository<ActualClient,Long> {

        List<ActualClient> findByLastName(String lastName);
        List<ActualClient> findBySubmissionDate(LocalDate date);
        List<ActualClient> findByFirstName(String name);
        List<ActualClient> findByStatus(String status);
        Page<ActualClient> findAll(Pageable pageable);


}
