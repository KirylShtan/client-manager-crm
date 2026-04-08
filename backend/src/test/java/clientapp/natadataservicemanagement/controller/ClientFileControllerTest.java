package clientapp.natadataservicemanagement.controller;


import clientapp.natadataservicemanagement.dto.ClientFileDto;
import clientapp.natadataservicemanagement.security.JwtAuthenticationFilter;
import clientapp.natadataservicemanagement.service.ClientFileService;
import clientapp.natadataservicemanagement.service.TelegramService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClientFileController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "SECURITY_USER_NAME=admin",
        "SECURITY_USER_PASSWORD=admin123",
        "JWT_SECRET_KEY=dummy-token",
        "SPRING_CLOUD_VAULT_URI=http://localhost:8200",
        "VAULT_SECRET_TOKEN=dummy-token"
})
public class ClientFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientFileService clientFileService;

    @MockBean
    private JwtAuthenticationFilter authenticationFilter;

    @MockBean
    private TelegramService telegramService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteFile_shouldReturnNoContent() throws Exception {
        doNothing().when(clientFileService).deleteFile(1L);

        mockMvc.perform(delete("/api/client_files/{fileId}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void uploadFile_shouldReturnUploadedFileDto() throws Exception {
        UUID clientUuid = UUID.randomUUID();

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "clientFile.json",
                "application/json",
                "test content".getBytes()
        );

        ClientFileDto fileDto = new ClientFileDto();
        fileDto.setId(1L);
        fileDto.setOriginalName(mockFile.getOriginalFilename());
        fileDto.setContentType(mockFile.getContentType());
        fileDto.setSize(mockFile.getSize());
        fileDto.setPreviewUrl("/api/files/1/preview");


        when(clientFileService.upload(any(UUID.class), any())).thenReturn(fileDto);

        mockMvc.perform(multipart("/api/client_files/upload")
                        .file(mockFile)
                        .param("clientUuid", clientUuid.toString())
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(fileDto.getId()))
                .andExpect(jsonPath("$.originalName").value(fileDto.getOriginalName()))
                .andExpect(jsonPath("$.size").value((long) fileDto.getSize()))
                .andExpect(jsonPath("$.contentType").value(fileDto.getContentType()))
                .andExpect(jsonPath("$.previewUrl").value(fileDto.getPreviewUrl()));
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void getClientFiles_shouldReturnListOfFiles() throws Exception {
        UUID clientUuid = UUID.randomUUID();

        ClientFileDto file1 = new ClientFileDto();
        file1.setId(1L);
        file1.setOriginalName("file1.json");
        file1.setContentType("application/json");
        file1.setSize(12L);
        file1.setPreviewUrl("/api/files/1/preview");

        ClientFileDto file2 = new ClientFileDto();
        file2.setId(2L);
        file2.setOriginalName("file2.json");
        file2.setContentType("application/json");
        file2.setSize(15L);
        file2.setPreviewUrl("/api/files/2/preview");

        when(clientFileService.getFiles(clientUuid)).thenReturn(List.of(file1, file2));

        mockMvc.perform(get("/api/client_files/{uuid}/files", clientUuid)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(file1.getId()))
                .andExpect(jsonPath("$[0].originalName").value(file1.getOriginalName()))
                .andExpect(jsonPath("$[0].size").value(file1.getSize().intValue()))
                .andExpect(jsonPath("$[0].contentType").value(file1.getContentType()))
                .andExpect(jsonPath("$[1].id").value(file2.getId()))
                .andExpect(jsonPath("$[1].originalName").value(file2.getOriginalName()))
                .andExpect(jsonPath("$[1].size").value(file2.getSize().intValue()))
                .andExpect(jsonPath("$[1].contentType").value(file2.getContentType()));
    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void downloadFile_shouldReturnFile() throws Exception {

        ClientFileDto fileDto = new ClientFileDto();
        fileDto.setId(1L);
        fileDto.setOriginalName("file1.json");
        fileDto.setContentType("application/json");
        fileDto.setSize(12L);

        org.springframework.core.io.Resource resource = new org.springframework.core.io.ByteArrayResource("test content".getBytes());

        when(clientFileService.getFileMetadata(anyLong())).thenReturn(fileDto);
        when(clientFileService.loadFile(anyLong())).thenReturn(resource);

        mockMvc.perform(get("/api/client_files/{id}/download", 1L)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Content-Disposition", "attachment; filename=\"" + fileDto.getOriginalName() + "\""))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().bytes("test content".getBytes()));
    }
}