package clientapp.natadataservicemanagement.model;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity
@Table(name = "archived_negative_clients")

public class NegativeClient extends Client {
    @Column(name = "archive_date")
    private LocalDate archiveDate;

    public LocalDate getArchiveDate() { return archiveDate; }
    public void setArchiveDate(LocalDate archiveDate) { this.archiveDate = archiveDate; }


}
