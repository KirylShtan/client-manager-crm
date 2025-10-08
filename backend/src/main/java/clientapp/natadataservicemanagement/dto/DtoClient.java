package clientapp.natadataservicemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;


import java.time.LocalDate;


public class DtoClient {

    private Long id;
    @NotBlank(message = "Name cannot be empty")
    private String firstName;

    @NotBlank(message = "SirName cannot be empty")
    private String lastName;
    @Size(message = "Size should be al least 5 symbols, 20 symbols max", min = 5 , max = 20)
    @NotBlank
    private String caseNumber;
    @NotNull(message = "date cannot be empty")
    @PastOrPresent(message = "You cannot use future date")
    private LocalDate submissionDate;
    @NotBlank(message = "status cannot be empty")
    private String status;



    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
