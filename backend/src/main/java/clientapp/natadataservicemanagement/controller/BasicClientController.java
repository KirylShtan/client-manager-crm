package clientapp.natadataservicemanagement.controller;
import clientapp.natadataservicemanagement.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
public abstract class BasicClientController<T> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected ClientService clientService;


    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public abstract ResponseEntity<T> searchClient(@RequestParam(required = false) Long id,
                                                   @RequestParam(required = false) String firstName,
                                                   @RequestParam(required = false) String lastName,
                                                   @RequestParam(required = false) String caseNumber,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate submissionDate,
                                                   @RequestParam(required = false) String status,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate archiveDate);

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public abstract List<T> getAllClients();

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public abstract ResponseEntity<Page<T>>getClientsPaged(@RequestParam (defaultValue = "0")int page,
                                                       @RequestParam (defaultValue = "10")int size,
                                                       @RequestParam(defaultValue = "id")String sortBy,
                                                       @RequestParam(defaultValue = "asc")String sortDir);
}






