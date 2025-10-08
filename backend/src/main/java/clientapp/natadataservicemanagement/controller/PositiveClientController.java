package clientapp.natadataservicemanagement.controller;

import clientapp.natadataservicemanagement.model.PositiveClient;
import clientapp.natadataservicemanagement.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/archived_positive_clients")
public class PositiveClientController extends BasicClientController {

    @Autowired
    private ClientService clientService;
    @Operation(
            summary = "Searching clients....",
            description = "Filtering clients  with id,lastName,firstName,caseNumber,status"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Clients have been found",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PositiveClient.class, type = "array")
            )),
            @ApiResponse(responseCode = "404",description = "Client haven't been found",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class))),


    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/search")
    @Override
    public ResponseEntity<?> searchClient(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String caseNumber,
            @RequestParam(required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate submissionDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate archiveDate) {
        logger.info("Receiving all clients from actual repo.. id={}, caseNumber={}, status={}",id,caseNumber,status);
        List<PositiveClient> allClients = clientService.getAllPositiveClients();
        logger.debug("Filtering parameters.. id={},firstName={},lastName={}, caseNumber={}, status={}",id,firstName,lastName,caseNumber,status);
        List<PositiveClient> clients = clientService.filterClients(allClients,id, firstName, lastName,
                caseNumber, submissionDate, status, archiveDate);
        if (clients.isEmpty()) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                    "No clients found with given parameters");
            problemDetail.setTitle("Clients not found!");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
        }
        logger.info("Filtering is finished successfully!");
        return ResponseEntity.ok(clients);
    }
    @Operation(
            summary = "Deleting client.....",
            description = "Deleting client using id"

    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Client successfully deleted",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PositiveClient.class)
            )),
            @ApiResponse(responseCode = "404",description = "Didn't found client with this id",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePositiveClient(@PathVariable Long id){
        try {
            clientService.deletePositiveClient(id);
            logger.info("Client id={} successfully deleted ", id);
            return ResponseEntity.ok("Client with id " + id + " successfully deleted!");
        }catch (EntityNotFoundException e){
            logger.warn("Attempted to delete client id={} but it was not found", id);
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                    "Client with such id didn't found!");
            problemDetail.setTitle("Client with such id doesn't exist ");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
        }
        catch (Exception e){
            logger.warn("Unexpected error");
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unpredictable error");
            problemDetail.setTitle("Unpredictable error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the client");
        }

    }
    @Operation(
            summary = "Getting all clients from positive repo....."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Listing all clients from actual repo....",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PositiveClient.class, type = "array")
            )),
            @ApiResponse(responseCode = "404",description = "Actual repo is empty, didn't found any client...",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/positive")
    @Override
    public List<PositiveClient> getAllClients(){
        List<PositiveClient> clients = clientService.getAllPositiveClients();
        if(clients.isEmpty()){
            logger.warn("No positive clients found in the repository");
            return clients;
        }
        clients.forEach(c -> logger.debug("Client : caseNumber = {}, status = {}",c.getCaseNumber(),c.getStatus()));
        return clients;
    }
    @Operation(
            summary = "updating client information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "client successfully updated ",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PositiveClient.class)
            )),
            @ApiResponse(responseCode = "404",description = "didn't found any client with such id",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(@PathVariable Long id,@RequestBody PositiveClient updatedPositiveClient){
        try{
            logger.info("Trying to update client with id={}",id);
            PositiveClient updated = clientService.updatePositiveClient(id,updatedPositiveClient);
            return new ResponseEntity<>(updated,HttpStatus.OK);
        }catch (EntityNotFoundException e){
            logger.warn("Client didn't found!");
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                    "Client didn't found!");
            problemDetail.setTitle("Client didn't found! ");
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
        }catch (Exception e){
            logger.warn("Unexpected error!");
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                    "unpredictable error");
            problemDetail.setTitle("unpredictable error");
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
        }
    }
    @Operation(
            summary = "paginating clients..."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Success!",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PositiveClient.class)
            )),
            @ApiResponse(responseCode = "500",description = "Unpredictable error",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/pospaginated")
    @Override
    public ResponseEntity<Page<PositiveClient>> getClientsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id")String sortBy,
            @RequestParam(defaultValue = "asc")String sortDir){
        logger.info("Fetching page {} of size {} sorted by {} {}", page, size, sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<PositiveClient> positiveClientPage = clientService.getAllPositiveClientsPaginated(pageable);
        return  new ResponseEntity<>(positiveClientPage,HttpStatus.OK);
    }

    }



