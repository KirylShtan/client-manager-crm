package clientapp.natadataservicemanagement.repository;

import clientapp.natadataservicemanagement.model.CompletedClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompletedClientRepository extends JpaRepository<CompletedClient,Long> {

}
