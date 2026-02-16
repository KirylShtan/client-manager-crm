package clientapp.natadataservicemanagement.service;



import clientapp.natadataservicemanagement.dto.ClientFileDto;
import clientapp.natadataservicemanagement.exception.CustomFileNotFoundException;
import clientapp.natadataservicemanagement.exception.FileReadException;
import clientapp.natadataservicemanagement.exception.FileStorageException;
import clientapp.natadataservicemanagement.model.ClientFile;
import clientapp.natadataservicemanagement.repository.ClientFileRepository;
import jakarta.transaction.Transactional;
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

@Service
public class ClientFileService {
  private static final Logger logger = LoggerFactory.getLogger(ClientFileService.class);
  private final ClientFileRepository clientFileRepository;

  @Autowired
  public ClientFileService(ClientFileRepository clientFileRepository){
      this.clientFileRepository = clientFileRepository;
  }

  public ClientFileDto upload(UUID clientUuid, MultipartFile file){
        logger.info("Uploading files for future use");
      try{
          Path uploadDir = Paths.get("uploads");
          Files.createDirectories(uploadDir);
          Path clientDir = uploadDir.resolve(clientUuid.toString());
          Files.createDirectories(clientDir);
          String storedName = UUID.randomUUID() +  " " + file.getOriginalFilename();
          Path filePath = uploadDir.resolve(storedName);
          Files.write(filePath,file.getBytes());
          ClientFile clientFile = new ClientFile();
          clientFile.setClientUuid(clientUuid);
          clientFile.setOriginalName(file.getOriginalFilename());
          clientFile.setStoredName(storedName);
          clientFile.setContentType(file.getContentType());
          clientFile.setSize(file.getSize());
          clientFile.setPath(filePath.toString());
          clientFile.setUploadedAt(LocalDateTime.now());

          clientFileRepository.save(clientFile);
          return mapToDto(clientFile);

      }catch (IOException e) {
          throw new FileStorageException("Loading error");
      }
  }
  public List<ClientFileDto> getFiles(UUID clientUuid){
      logger.info("Getting unique key for files");
      List<ClientFile> files = clientFileRepository.findByClientUuid(clientUuid);
      return files.stream()
              .map(this::mapToDto)
              .collect(Collectors.toList());

  }
  private ClientFileDto mapToDto(ClientFile file){
      logger.info("converting map to DTO");
      ClientFileDto dto = new ClientFileDto();
      dto.setId(file.getId());
      dto.setOriginalName(file.getOriginalName());
      dto.setSize(file.getSize());
      dto.setContentType(file.getContentType());
      dto.setPreviewUrl("/api/files" + file.getId() + "/preview");
      return dto;
  }
  public Resource loadFile(Long id)  {
      ClientFile clientFile = clientFileRepository.findById(id)
              .orElseThrow(() -> new CustomFileNotFoundException("File didn't found!"));
      try{
          Path path = Paths.get(clientFile.getPath());
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
      ClientFile clientFile = clientFileRepository.findById(fileId)
              .orElseThrow(() -> new CustomFileNotFoundException("File not found"));
      Path path = Paths.get(clientFile.getPath());
      try{
          if (Files.exists(path)){
              Files.delete(path);
          }
      }catch (IOException e){
          throw new FileReadException("Failed to delete file from storage",e);
      }
      clientFileRepository.delete(clientFile);

  }
    public ClientFileDto getFileMetadata(Long id){
        ClientFile clientFile = clientFileRepository.findById(id)
                .orElseThrow(() -> new CustomFileNotFoundException("File not found!"));
        logger.info("Preparing file for preview");
        ClientFileDto dto = new ClientFileDto();
        dto.setId(clientFile.getId());
        dto.setOriginalName(clientFile.getOriginalName());
        dto.setContentType(clientFile.getContentType());
        dto.setSize(clientFile.getSize());
        dto.setPreviewUrl("/api/files/" + clientFile.getId() + "/preview"); // если нужен preview
        return dto;
    }


}
