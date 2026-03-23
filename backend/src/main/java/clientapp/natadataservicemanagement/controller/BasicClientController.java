package clientapp.natadataservicemanagement.controller;
import clientapp.natadataservicemanagement.service.ActualClientService;
import clientapp.natadataservicemanagement.service.CompletedClientService;
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




public abstract class BasicClientController<T> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());




    public abstract ResponseEntity<List<T>> searchClient(@RequestParam(required = false) Long id,
                                                   @RequestParam(required = false) String firstName,
                                                   @RequestParam(required = false) String lastName,
                                                   @RequestParam(required = false) String caseNumber,
                                                   @RequestParam(required = false) String submissionDate,
                                                   @RequestParam(required = false) String status,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate archiveDate,
                                                   @RequestParam(required = false) String companyName,
                                                   @RequestParam(required = false) String payed,
                                                   @RequestParam(required = false) Boolean result

    );


    public abstract ResponseEntity<List<T>> getAllClients();


    public abstract ResponseEntity<Page<T>>getClientsPaged(@RequestParam (defaultValue = "0")int page,
                                                       @RequestParam (defaultValue = "10")int size,
                                                       @RequestParam(defaultValue = "id")String sortBy,
                                                       @RequestParam(defaultValue = "asc")String sortDir);



}






