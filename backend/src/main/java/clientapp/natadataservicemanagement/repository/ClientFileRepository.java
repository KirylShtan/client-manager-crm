package clientapp.natadataservicemanagement.repository;

import clientapp.natadataservicemanagement.model.ClientFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClientFileRepository extends JpaRepository<ClientFile,Long> {
    List<ClientFile> findByClientUuid(UUID clientUuid);
}
