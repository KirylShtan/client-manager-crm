package clientapp.natadataservicemanagement.repository;

import clientapp.natadataservicemanagement.model.ActualClient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
    public interface ActualClientRepository extends JpaRepository<ActualClient,Long> {
        Page<ActualClient> findAll(Pageable pageable);
        Optional<ActualClient> findTopByOrderByIdDesc();
        @Query("SELECT c FROM ActualClient c WHERE c.submissionDate BETWEEN :start AND :end")
        List<ActualClient> findBySubmissionDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);






    }
