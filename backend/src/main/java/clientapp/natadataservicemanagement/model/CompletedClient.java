package clientapp.natadataservicemanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "сompleted_clients")
public class CompletedClient extends Client {
    @Column(name = "archive_client")
    private LocalDate archiveClient;

}
