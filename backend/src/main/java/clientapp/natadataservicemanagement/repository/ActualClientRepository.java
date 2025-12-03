package clientapp.natadataservicemanagement.repository;

import clientapp.natadataservicemanagement.model.ActualClient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
    public interface ActualClientRepository extends JpaRepository<ActualClient,Long> {
        Page<ActualClient> findAll(Pageable pageable);



    }
