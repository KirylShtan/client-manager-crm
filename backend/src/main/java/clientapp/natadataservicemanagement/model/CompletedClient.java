package clientapp.natadataservicemanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "сompleted_clients")
public class CompletedClient extends Client {
    @Column(name = "archive_client")
    private LocalDate archiveClient;

    public LocalDate getArchiveClient() {
        return archiveClient;
    }

    public void setArchiveClient(LocalDate archiveClient) {
        this.archiveClient = archiveClient;
    }
}
