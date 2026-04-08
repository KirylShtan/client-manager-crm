package clientapp.natadataservicemanagement.service;

import clientapp.natadataservicemanagement.dto.ClientFileDto;
import clientapp.natadataservicemanagement.exception.CustomFileNotFoundException;
import clientapp.natadataservicemanagement.model.ClientFile;
import clientapp.natadataservicemanagement.repository.ClientFileRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientFileServiceTest {
    @Mock
    private ClientFileRepository clientFileRepository;

    @InjectMocks
    private ClientFileService clientFileService;

    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException{
        tempDir = Files.createTempDirectory("uploads");
        System.setProperty("user.dir",tempDir.toString());
    }

    @AfterEach
    void tearDown() throws  IOException{
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try{
                        Files.deleteIfExists(path);
                    }catch (IOException ignored){

                    }
                });
    }

    @Test
    void uploadFile_ShouldSaveFileAndMetaData(){
        UUID clientUuid = UUID.randomUUID();
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello world".getBytes()
        );
        when(clientFileRepository.save(any(ClientFile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ClientFileDto dto = clientFileService.upload(clientUuid,mockFile);
        assertNotNull(dto);
        assertEquals("test.txt",dto.getOriginalName());
        assertEquals("text/plain",dto.getContentType());
        assertEquals(11,dto.getSize());

        verify(clientFileRepository).save(any(ClientFile.class));

    }

    @Test
    void getFilesShouldReturnDto(){
        UUID clientUuid = UUID.randomUUID();
        ClientFile file = new ClientFile();
        file.setId(1L);
        file.setClientUuid(clientUuid);
        file.setOriginalName("doc.pdf");
        file.setSize(100L);
        file.setContentType("application/pdf");

        when(clientFileRepository.findByClientUuid(clientUuid)).thenReturn(List.of(file));

        List<ClientFileDto> result = clientFileService.getFiles(clientUuid);

        assertEquals(1,result.size());
        assertEquals("doc.pdf",result.get(0).getOriginalName());
    }

    @Test
    void loadFile_shouldReturnResource() throws Exception{
        Path filePath = Files.createTempFile("test",".txt");
        Files.writeString(filePath,"data");
        ClientFile clientFile = new ClientFile();
        clientFile.setId(1L);
        clientFile.setPath(filePath.toString());
        when(clientFileRepository.findById(1L))
                .thenReturn(Optional.of(clientFile));

        Resource resource = clientFileService.loadFile(1L);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());

    }
    @Test
    void loadFile_shouldThrowWhenNotFound(){
        when(clientFileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomFileNotFoundException.class, () -> clientFileService.loadFile(1L));
    }

    @Test
    void deleteFile_shouldRemoveFileAndDbEntry() throws Exception{
        Path filePath = Files.createTempFile("delete",".txt");
        ClientFile clientFile = new ClientFile();
        clientFile.setId(1L);
        clientFile.setPath(filePath.toString());
        when(clientFileRepository.findById(1L)).thenReturn(Optional.of(clientFile));

        clientFileService.deleteFile(1L);

        assertFalse(Files.exists(filePath));
        verify(clientFileRepository).delete(clientFile);
    }

    @Test
    void getFileMetaData_shouldReturnDto(){
        ClientFile clientFile = new ClientFile();
        clientFile.setId(5L);
        clientFile.setOriginalName("image.png");
        clientFile.setContentType("image/png");
        clientFile.setSize(200L);

        when(clientFileRepository.findById(5L)).thenReturn(Optional.of(clientFile));

        ClientFileDto dto = clientFileService.getFileMetadata(5L);
        assertEquals("image.png",dto.getOriginalName());
        assertEquals("image/png",dto.getContentType());
        assertEquals(200L,dto.getSize());


    }



}
