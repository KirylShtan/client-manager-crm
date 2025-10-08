package clientapp.natadataservicemanagement.controller;


import clientapp.natadataservicemanagement.dto.DtoActualClient;
import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
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
public class ActualClientController extends BasicClientController {


    @Autowired
    private ClientService clientService;
    @Operation(
            summary = "Searching clients....",
            description = "Filtering clients  with id,lastName,firstName,caseNumber,status"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Clients have been found",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActualClient.class, type = "array")
            )),
            @ApiResponse(responseCode = "404",description = "Client haven't been found",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation =ProblemDetail.class)
            ))

    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/search")
    @Override
    public ResponseEntity<?> searchClient(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String caseNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate submissionDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate archiveDate) {
        logger.info("Receiving all clients from actual repo.. id={}, caseNumber={}, status={}",id,caseNumber,status);
        List<ActualClient> allClients = clientService.getAllClients();
        logger.debug("Filtering parameters.. id={},firstName={},lastName={}, caseNumber={}, status={}",id,firstName,lastName,caseNumber,status);
        List<ActualClient> clients = clientService.filterClients(allClients, id, firstName, lastName,
                caseNumber, submissionDate, status, archiveDate);
        if (clients.isEmpty()) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                    "No clients found with given parameters");
            problemDetail.setTitle("Clients not found!");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
        }
        logger.info("Filtering is finished successfully! ");
        return ResponseEntity.ok(clients);
    }
    @Operation(
            summary = "Add a client",
            description = "Adding new client to database... Returning fresh created client"
    )
    @ApiResponses(value ={
            @ApiResponse(responseCode = "201",description = "Client successfully created",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActualClient.class)
            )),
            @ApiResponse(responseCode = "404",description = "Validation Error",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "500",description = "Unpredictable Error",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ActualClient> addClient(@Valid @RequestBody DtoActualClient dtoClient){
        logger.debug("Validation started...,firstName={},lastName={},caseNumber={},submissionDate={},status={}"
                ,dtoClient.getFirstName(),dtoClient.getLastName(),dtoClient.getCaseNumber(),dtoClient.getSubmissionDate(),dtoClient.getStatus());
        ActualClient actualClient = clientService.addedActualClientFromDto(dtoClient);
        logger.info("Validation successfully finished");
        return new ResponseEntity<>(actualClient,HttpStatus.CREATED);

    }
    @Operation(
            summary = "Getting all clients from Actual repo....."

    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Listing all clients from actual repo....",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActualClient.class, type = "array")
            )),
            @ApiResponse(responseCode = "404",description = "Actual repo is empty, didn't found any client...",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/actual")
    @Override
    public List<ActualClient> getAllClients() {
        List<ActualClient> clients = clientService.getAllClients();
        if (clients.isEmpty()) {
            logger.warn("No actual clients found in the repository");
            return clients;
        }
        clients.forEach(c -> logger.debug("Client : caseNumber = {}, status = {}",c.getCaseNumber(),c.getStatus()));
        return clients;
    }
    @Operation(
            summary = "Deleting client.....",
            description = "Deleting client using id"

    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Client successfully deleted",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActualClient.class)
            )),
            @ApiResponse(responseCode = "404",description = "Didn't found client with this id",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable Long id) {
        try {
            clientService.deleteClient(id);
            logger.info("Client id={} successfully deleted ", id);
            return ResponseEntity.ok("Client successfully deleted!");
        } catch (EntityNotFoundException e) {
            logger.warn("Attempted to delete client id={} but it was not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client with id " + id + " didn't found!");
        }catch (Exception e){
            logger.warn("Unexpected error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }
    @Operation(
            summary = "Putting client into positive/negative repo.....",
            description = "Putting client into archive with id as positive/negative result"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "client with such id was successfully archived",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActualClient.class)
            )),
            @ApiResponse(responseCode = "404",description = "didn't found any client with such id",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/archive")
    public ResponseEntity<?> archiveClient(@PathVariable("id") Long id, @RequestParam boolean isPositive) {
        try {
          logger.info("Archiving client with id={} as {}", id , isPositive ? "positive" : "negative");
        clientService.archiveClient(id, isPositive);
      return new ResponseEntity<>("Client in archive", HttpStatus.OK);
    } catch (EntityNotFoundException e) {
          ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,"Client with  id " + id + " didn't found!");
            problemDetail.setTitle("Client didn't found!");
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
      }catch (Exception e){
          ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR
          ,"Unexpected error");
            problemDetail.setTitle("Unexpected error");
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
    }
    @Operation(
            summary = "updating client information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "client successfully updated ",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActualClient.class)
            )),
            @ApiResponse(responseCode = "404",description = "didn't found any client with such id",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation =ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(@PathVariable Long id, @RequestBody ActualClient updatedClient){
        try{
            logger.info("Updating Client with id={}",id);
            ActualClient updated = clientService.updateActualClient(id,updatedClient);
            return  new ResponseEntity<>(updated,HttpStatus.OK);
        }catch (EntityNotFoundException e){
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                    "No clients found with given parameters");
            problemDetail.setTitle("Clients not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);

        }catch (Exception e){
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR," " +
                    "unexpected error while updating");
            problemDetail.setTitle("Update error");
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
        }
    }
    @Operation(
            summary = "paginating clients..."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Success!",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActualClient.class)
            )),
            @ApiResponse(responseCode = "500",description = "Unpredictable error",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/paginated")
    @Override
    public ResponseEntity<Page<ActualClient>>getClientsPaged(
            @RequestParam (defaultValue = "0")int page,
            @RequestParam (defaultValue = "10")int size,
            @RequestParam(defaultValue = "id")String sortBy,
            @RequestParam(defaultValue = "asc")String sortDir){
        logger.info("Fetching page {} of size {} sorted by {} {}", page, size, sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ActualClient> actualClientPage = clientService.getAllActualClientsPaginated(pageable);
        return  ResponseEntity.ok(actualClientPage);

    }


}





