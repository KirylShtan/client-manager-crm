package clientapp.natadataservicemanagement.dto;

import java.time.LocalDate;

public class DtoPositiveClient extends DtoClient {
    private LocalDate archiveDate;

    public LocalDate getArchiveDate() { return archiveDate; }
    public void setArchiveDate(LocalDate archiveDate) { this.archiveDate = archiveDate; }
}
