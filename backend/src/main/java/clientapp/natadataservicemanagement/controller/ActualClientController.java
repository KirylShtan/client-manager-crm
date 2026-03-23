package clientapp.natadataservicemanagement.controller;


import clientapp.natadataservicemanagement.dto.DtoActualClient;
import clientapp.natadataservicemanagement.dto.DtoNote;

import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.service.ActualClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

import java.util.List;


@RestController
@RequestMapping("/api/ActualClients")
@CrossOrigin(origins = "http://localhost:3000")
public class ActualClientController extends BasicClientController<ActualClient> {

    private static final Logger logger = LoggerFactory.getLogger(ActualClientController.class);
    private final ActualClientService service;

    @Autowired
    public ActualClientController(ActualClientService clientService) {
        this.service = clientService;

    }

    @Operation(
            summary = "Search clients",
            description = "Filter clients by id, last name, first name, case number and status"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Search result (may be empty)",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ActualClient.class)
                            ))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid filter parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/search")
    @Override
    public ResponseEntity<List<ActualClient>> searchClient(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String caseNumber,
            @RequestParam(required = false) String submissionDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate archiveDate,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String payed,
            @RequestParam(required = false) Boolean result
    ) {
        List<ActualClient> filteredClients = service.searchClients(
                id, firstName, lastName, caseNumber,
                submissionDate, status, archiveDate,
                companyName, payed, result
        );
        return ResponseEntity.ok(filteredClients);
    }

    @Operation(
            summary = "Add a client",
            description = "Adding new client to database, returning fresh created client"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client successfully created", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActualClient.class)
            )),
            @ApiResponse(responseCode = "400", description = "Validation Error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "500", description = "Unpredictable Error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ActualClient> addClient(@Valid @RequestBody DtoActualClient dtoClient) {
        logger.debug("Validation started...,firstName={},lastName={},caseNumber={},submissionDate={},status={},companyName={}"
                , dtoClient.getFirstName(), dtoClient.getLastName(), dtoClient.getCaseNumber()
                , dtoClient.getSubmissionDate(), dtoClient.getStatus(), dtoClient.getCompanyName());
        ActualClient actualClient = service.addActualClientFromDto(dtoClient);
        logger.info("Validation successfully finished");
        return new ResponseEntity<>(actualClient, HttpStatus.CREATED);

    }

    @Operation(
            summary = "Getting all clients from Actual repository"

    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Listing all clients from actual repository",
                    content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActualClient.class, type = "array")
            )),
            @ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/actual")
    @Override
    public ResponseEntity<List<ActualClient>> getAllClients() {
        List<ActualClient> clients = service.getAllClientsOrThrow();
        logger.info("Fetched {} actual clients", clients.size());
        return ResponseEntity.ok(clients);
    }

    @Operation(
            summary = "Deleting client from actual repository",
            description = "Deleting client using id"

    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Client successfully deleted", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActualClient.class)
            )),
            @ApiResponse(responseCode = "404", description = "Didn't found client with this id", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable Long id) {
            service.deleteWithDependencies(id);
            logger.info("Client id={} successfully deleted ", id);
            return ResponseEntity.ok("Client successfully deleted!");

    }

    @Operation(
            summary = "Putting client into completed repository",
            description = "Putting client into archive with id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "client with such id was successfully archived", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActualClient.class)
            )),
            @ApiResponse(responseCode = "404", description = "didn't found any client with such id", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/archive")
    public ResponseEntity<String> archiveClient(@PathVariable Long id) {
        logger.info("Archiving client with id={}", id);
        service.archiveClient(id);
        logger.info("Client with id={} successfully archived", id);
        return ResponseEntity.ok("Client in archive");
    }



    @Operation(
            summary = "updating client information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "client successfully updated ", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActualClient.class)
            )),
            @ApiResponse(responseCode = "404", description = "didn't found any client with such id", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(@PathVariable Long id, @RequestBody ActualClient updatedClient) {
            logger.info("Updating Client with id={}", id);
            ActualClient updated = service.updateClient(id, updatedClient);
            logger.info("Client with id={} successfully updated", id);
            return new ResponseEntity<>(updated, HttpStatus.OK);

    }

    @Operation(
            summary = "paginating clients..."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success!", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActualClient.class)
            )),
            @ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/paginated")
    @Override
    public ResponseEntity<Page<ActualClient>> getClientsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        logger.info("Fetching page {} of size {} sorted by {} {}", page, size, sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ActualClient> actualClientPage = service.getAllClientsPaginated(pageable);
        return ResponseEntity.ok(actualClientPage);

    }


    @Operation(
            summary = "updating details for actual client"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success!", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActualClient.class)
            )),
            @ApiResponse(responseCode = "500", description = "Unpredictable error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "404", description = "Didn't found any client with such id",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}/notes")
    public ResponseEntity<ActualClient> updateActualClientNote(@PathVariable Long id,
                                                               @RequestBody DtoNote dtoNote) {
        ActualClient updatedClient = service.updateClientNote(id, dtoNote.getNote());
        logger.debug("Saving note: {} for client id: {}", dtoNote.getNote(), id);
        return ResponseEntity.ok(updatedClient);
    }
    @Operation(
            summary = "getting details for actual client"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success!", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActualClient.class)
            )),
            @ApiResponse(responseCode = "500", description = "Unpredictable error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))

    })
    @GetMapping("/actualNode/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ActualClient> getActualClientNote(@PathVariable Long id) {
        ActualClient client = service.getClientNoteOrThrow(id);
        return ResponseEntity.ok(client);
    }
    @Operation(
            summary = "getting details for actual client"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success!", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActualClient.class)
            )),
            @ApiResponse(responseCode = "500", description = "Unpredictable error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "400", description = "Bad input data", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @GetMapping("/complexDate")
    public ResponseEntity<List<ActualClient>> getActualClientsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<ActualClient> clients = service.findClientsBetweenDates(startDate, endDate);
        return ResponseEntity.ok(clients);
    }

    @Operation(
            summary = "getting actual status of case"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success!",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Void.class)
            )),
            @ApiResponse(responseCode = "500",description = "Unpredictable error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "400", description = "Bad input data",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "404", description = "No client with such case number or password",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @GetMapping("/check-status/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Void> checkClientStatus(@PathVariable Long id){
        DtoActualClient dto = service.getDtoById(id);
        service.openStatusPageWithCredentials(dto);
        return ResponseEntity.ok().build();
    }
}