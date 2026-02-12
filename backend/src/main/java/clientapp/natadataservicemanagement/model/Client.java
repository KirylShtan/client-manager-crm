package clientapp.natadataservicemanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@MappedSuperclass
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String caseNumber;
    private LocalDate submissionDate;
    private String status;
    @Transient
    private LocalDate archiveDate;
    @Column(length = 255)
    private String note;
    private String companyName;
    private String payed;
    @Column(nullable = false,unique = true,updatable = false)
    private UUID clientUuid;


}
