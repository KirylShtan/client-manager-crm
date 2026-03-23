package clientapp.natadataservicemanagement.controller;

import clientapp.natadataservicemanagement.dto.DtoNote;
import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.model.CompletedClient;
import clientapp.natadataservicemanagement.service.CompletedClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
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
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/completed_clients")
@CrossOrigin(origins = "http://localhost:3000")
public class CompletedClientController extends BasicClientController<CompletedClient> {

    private static final Logger logger = LoggerFactory.getLogger(CompletedClientController.class);
    private final CompletedClientService completedClientService;

    @Autowired
    public CompletedClientController(CompletedClientService completedClientService) {
        this.completedClientService = completedClientService;
    }
    @Operation(
            summary = "Searching clients...",
            description = "Filtering clients  with id,lastName,firstName,caseNumber,status"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clients have been found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CompletedClient.class, type = "array")
            )),
    @ApiResponse(responseCode = "404", description = "Clients haven't been found", content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CompletedClient.class, type = "array")
    ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/completed_search/search")
    @Override
    public ResponseEntity<List<CompletedClient>> searchClient(
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
        List<CompletedClient> filteredClients = completedClientService.searchClients(
                id, firstName, lastName, caseNumber,
                submissionDate, status, archiveDate,
                companyName, payed, result
        );
        return ResponseEntity.ok(filteredClients);
    }

    @Operation(
            summary = "Deleting client.....",
            description = "Deleting client using id"

    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Client successfully deleted",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CompletedClient.class)
            )),
            @ApiResponse(responseCode = "404",description = "Didn't found client with this id",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompletedClient(@PathVariable Long id) {
            completedClientService.deleteClient(id);
            logger.info("Deleting client with id {} successfully", id);
            return ResponseEntity.noContent().build();

    }

    @Operation(
            summary = "Getting all clients from negative repo....."

    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Listing all clients from actual repo....",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CompletedClient.class, type = "array")
            )),
            @ApiResponse(responseCode = "500",description = "Internal Server Error",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/all_completed_clients")
    @Override
    public ResponseEntity<List<CompletedClient>> getAllClients(){
        List<CompletedClient> clients = completedClientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @Operation(
            summary = "updating client information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "client successfully updated ",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CompletedClient.class)
            )),
            @ApiResponse(responseCode = "404",description = "didn't found any client with such id",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(@PathVariable Long id, @RequestBody CompletedClient updatedClient) {
            logger.info("Updating Client with id={}", id);
            CompletedClient updated = completedClientService.updateClient(id, updatedClient);
            return new ResponseEntity<>(updated, HttpStatus.OK);

    }

    @Operation(
            summary = "paginating clients..."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Success!",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CompletedClient.class)
            )),
            @ApiResponse(responseCode = "500",description = "Unpredictable error",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/paginated")
    @Override
    public ResponseEntity<Page<CompletedClient>> getClientsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        logger.info("Fetching page {} of size {} sorted by {} {}", page, size, sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<CompletedClient> completedClientPage = completedClientService.getAllClientsPaginated(pageable);
        return  new ResponseEntity<>(completedClientPage, HttpStatus.OK);

    }
    @Operation(
            summary = "updating details for negative client..."
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
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/completedNoteUpdate/{id}")
    public ResponseEntity<CompletedClient> updateCompletedClientNote(@PathVariable Long id, @RequestBody DtoNote dtoNote) {
        CompletedClient updatedClient = completedClientService.updateClientNote(id,dtoNote.getNote());
        logger.debug("Saving note: {} for client id: {}", dtoNote.getNote(), id);
        return ResponseEntity.ok(updatedClient);
    }

    @Operation(
            summary = "getting details for negative client..."
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
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/Note/{id}")
    public ResponseEntity<CompletedClient> getCompletedClientNote(@PathVariable Long id){
        CompletedClient client = completedClientService.getClientNote(id);
        return ResponseEntity.ok(client);

    }

}
