package clientapp.natadataservicemanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
public class DtoClient {

    private Long id;
    @NotBlank(message = "Name cannot be empty")
    private String firstName;

    @NotBlank(message = "lastName cannot be empty")
    private String lastName;
    @Size(message = "Size should be al least 5 symbols, 20 symbols max", min = 5 , max = 20)
    @NotBlank
    private String caseNumber;
    @NotNull(message = "date cannot be empty")
    @PastOrPresent(message = "You cannot use future date")
    private LocalDate submissionDate;
    @NotBlank(message = "status cannot be empty")
    private String status;
    @Getter
    @NotBlank(message = "company cannot be empty")
    private String companyName;
    @Column(unique = true, nullable = false, updatable = false)
    private UUID clientUuid = UUID.randomUUID();

    private boolean notifyEmail;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String realPassword;
    private String vaultKey;







}
