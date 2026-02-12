package clientapp.natadataservicemanagement.controller;

import clientapp.natadataservicemanagement.dto.ClientFileDto;
import clientapp.natadataservicemanagement.exception.CustomFileNotFoundException;
import clientapp.natadataservicemanagement.exception.FileStorageException;
import clientapp.natadataservicemanagement.model.ClientFile;
import clientapp.natadataservicemanagement.repository.ClientFileRepository;
import clientapp.natadataservicemanagement.service.ClientFileService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.hibernate.annotations.CollectionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/client_files")
public class ClientFileController {

    private static final Logger logger = LoggerFactory.getLogger(ClientFileController.class);

    private final ClientFileService clientFileService;


    @Autowired
    public ClientFileController(ClientFileService clientFileService){
        this.clientFileService = clientFileService;

    }
    @Operation(summary = "Upload documents",
               description = "uploading documents using Unique final Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "File successfully uploaded", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ClientFileDto.class)
            )),
            @ApiResponse(responseCode = "400",description = "bad request", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "500",description = "Internal Server Error",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/upload")
    public ResponseEntity<ClientFileDto> upload(@RequestParam UUID clientUuid,
                       @RequestParam MultipartFile file){
        logger.info("Initializing new DTO for uploading");
        ClientFileDto dto = clientFileService.upload(clientUuid,file);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);

        }
    @Operation(summary = "Getting all files", description = "getting all files as array")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ClientFileController.class)
            )),
            @ApiResponse(responseCode = "500",description = "Internal Server Error",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/client/{uuid}")
    public List<ClientFileDto> getClientFiles(@PathVariable UUID uuid){
        logger.info("Getting full list of files");
        return clientFileService.getFiles(uuid);
    }
    @Operation(summary = "Download files", description = "Downloading files according to it's ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ClientFileController.class)
            )),
            @ApiResponse(responseCode = "404", description = "File doesn't found",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "500",description = "Internal Server Error",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download (@PathVariable Long id) {
        logger.info("Getting FileMetadata by using clientFileService");
        ClientFileDto clientFile = clientFileService.getFileMetadata(id);
        logger.info("Loading file....");
        Resource resource = clientFileService.loadFile(id);
        return  ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(clientFile.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + clientFile.getOriginalName() + "\"")
                .body(resource);

    }
    @Operation(summary = "Deleting file", description = "Deleting file using current id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "file successfully deleted", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ClientFileController.class)
            )),
            @ApiResponse(responseCode = "404", description = "file not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId){
        logger.info("Trying to delete file");
        clientFileService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }


}
