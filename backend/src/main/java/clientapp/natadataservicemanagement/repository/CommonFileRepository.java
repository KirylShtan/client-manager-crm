package clientapp.natadataservicemanagement.repository;

import clientapp.natadataservicemanagement.model.CommonFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommonFileRepository  extends JpaRepository<CommonFile,Long> {
    
}