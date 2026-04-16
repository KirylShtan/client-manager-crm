package clientapp.natadataservicemanagement.service;



import clientapp.natadataservicemanagement.dto.CommonFileDto;
import clientapp.natadataservicemanagement.exception.CustomFileNotFoundException;
import clientapp.natadataservicemanagement.exception.FileReadException;
import clientapp.natadataservicemanagement.model.CommonFile;
import clientapp.natadataservicemanagement.repository.CommonFileRepository;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;


@Service
public class CommonFileService {
    private static final Logger logger = LoggerFactory.getLogger(CommonFileService.class);
    private final CommonFileRepository commonFileRepository;

    @Autowired
    public CommonFileService(CommonFileRepository commonFileRepository){
        this.commonFileRepository = commonFileRepository;
    }

    public CommonFileDto upload(MultipartFile file) {
    logger.info("Uploading file: {}", file.getOriginalFilename());
    if (file == null || file.isEmpty()) {
        throw new FileReadException("File is empty",null);
    }
    try {
        Path uploadDir = Paths.get("common_uploads").toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);
        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String cleanedName = StringUtils.cleanPath(originalName);
        if (cleanedName.contains("..")) {
            throw new FileReadException("Invalid file name",null);
         }
        
        String safeName = Paths.get(cleanedName).getFileName().toString();
        if (safeName.isBlank()) {
            throw new FileReadException("Invalid file name",null);
         }
        String storedName = UUID.randomUUID() + "_" + safeName;
        Path filePath = uploadDir.resolve(storedName).normalize();
        if (!filePath.startsWith(uploadDir)) {
            throw new FileReadException("Invalid file path",null);
            }
        Files.write(filePath, file.getBytes());
        logger.info("File saved to: {}", filePath);
        CommonFile commonFile = new CommonFile();
        commonFile.setOriginalName(safeName);
        commonFile.setStoredName(storedName);
        commonFile.setContentType(file.getContentType());
        commonFile.setSize(file.getSize());
        commonFile.setPath(filePath.toString());
        commonFile.setUploadedAt(LocalDateTime.now());
        CommonFile saved = commonFileRepository.save(commonFile);
        return mapToDto(saved);
        } catch (IOException e) {
        logger.error("File upload failed", e);
        throw new FileReadException("Failed to upload file", e);
        }
    }


    public List<CommonFileDto> getFiles(){
        logger.info("Getting all common files");
        List <CommonFile> files = commonFileRepository.findAll();
        return files.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private CommonFileDto mapToDto(CommonFile file){
      logger.debug("converting map to DTO");
      CommonFileDto dto = new CommonFileDto();
      dto.setId(file.getId());
      dto.setOriginalName(file.getOriginalName());
      dto.setSize(file.getSize());
      dto.setContentType(file.getContentType());
      dto.setPreviewUrl("/api/common_files/" + file.getId() + "/preview");
      return dto;
  }
    
    public Resource loadFile(Long id)  {
      CommonFile commonFile = commonFileRepository.findById(id)
              .orElseThrow(() -> new CustomFileNotFoundException("File wasn't found"));
      try{
          Path path = Paths.get(commonFile.getPath());
          Resource resource =  new UrlResource(path.toUri());
          if (!resource.exists() || !resource.isReadable()){
              throw new FileReadException("Can't read the file",null);
          }
          return resource;
      }catch (MalformedURLException e){
          throw new FileReadException("File download error",e);
      }
  }

    @Transactional
    public void deleteFile(Long fileId){
      CommonFile commonFile = commonFileRepository.findById(fileId)
              .orElseThrow(() -> new CustomFileNotFoundException("File not found"));
      Path path = Paths.get(commonFile.getPath());
      try{
          if (Files.exists(path)){
              Files.delete(path);
          }
      }catch (IOException e){
          throw new FileReadException("Failed to delete file from storage",e);
      }
      commonFileRepository.delete(commonFile);

  }

        public CommonFileDto getFileMetadata(Long id){
        CommonFile commonFile = commonFileRepository.findById(id)
                .orElseThrow(() -> new CustomFileNotFoundException("File not found!"));
        logger.info("Preparing file for preview");
        CommonFileDto dto = new CommonFileDto();
        dto.setId(commonFile.getId());
        dto.setOriginalName(commonFile.getOriginalName());
        dto.setContentType(commonFile.getContentType());
        dto.setSize(commonFile.getSize());
        dto.setPreviewUrl("/api/common_files/" + commonFile.getId() + "/preview"); 
        return dto;
    }

    
    }
