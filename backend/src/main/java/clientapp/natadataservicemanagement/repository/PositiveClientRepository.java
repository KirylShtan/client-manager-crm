package clientapp.natadataservicemanagement.repository;


import clientapp.natadataservicemanagement.model.PositiveClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PositiveClientRepository extends JpaRepository<PositiveClient,Long> {

    Page<PositiveClient> findAll(Pageable pageable);
}
