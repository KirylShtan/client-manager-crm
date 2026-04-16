package clientapp.natadataservicemanagement.controller;

import clientapp.natadataservicemanagement.dto.CommonFileDto;
import clientapp.natadataservicemanagement.exception.CustomFileNotFoundException;
import clientapp.natadataservicemanagement.exception.FileStorageException;
import clientapp.natadataservicemanagement.model.CommonFile;
import clientapp.natadataservicemanagement.repository.CommonFileRepository;
import clientapp.natadataservicemanagement.service.CommonFileService;


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
@RequestMapping("/api/common_files")
public class CommonFileController {
    private static final Logger logger = LoggerFactory.getLogger(CommonFileController.class);
    private final CommonFileService commonFileService;

    @Autowired
    public CommonFileController(CommonFileService commonFileService){
        this.commonFileService = commonFileService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<CommonFileDto> upload (@RequestParam("file") MultipartFile file) {
        logger.info("Initializing new DTO for uploading");
        CommonFileDto dto = commonFileService.upload(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);

    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public List<CommonFileDto> getFiles(){
        logger.info("Getting full list of common files");
        return commonFileService.getFiles();
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download (@PathVariable Long id) {
        logger.info("Getting FileMetadata by using commonFileService");
        CommonFileDto commonFile = commonFileService.getFileMetadata(id);
        logger.info("Loading file....");
        Resource resource = commonFileService.loadFile(id);
        return  ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(commonFile.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + commonFile.getOriginalName() + "\"")
                .body(resource);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId){
        logger.info("Trying to delete file");
        commonFileService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }
}




