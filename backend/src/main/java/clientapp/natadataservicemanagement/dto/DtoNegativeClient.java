package clientapp.natadataservicemanagement.dto;

import java.time.LocalDate;

public class DtoNegativeClient extends DtoClient {
    private LocalDate archiveDate;

    public LocalDate getArchiveDate() { return archiveDate; }
    public void setArchiveDate(LocalDate archiveDate) { this.archiveDate = archiveDate; }
}
