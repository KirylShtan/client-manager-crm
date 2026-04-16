package clientapp.natadataservicemanagement.controller;
import clientapp.natadataservicemanagement.dto.DtoActualClient;
import clientapp.natadataservicemanagement.dto.DtoNote;
import clientapp.natadataservicemanagement.exception.GlobalExceptionHandlerIssueDetail;
import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.security.JwtService;
import clientapp.natadataservicemanagement.service.ActualClientService;
import clientapp.natadataservicemanagement.service.VaultService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(controllers = ActualClientController.class)
@AutoConfigureMockMvc(addFilters = false) // Docker/CI-friendly slice tests
@Import(GlobalExceptionHandlerIssueDetail.class)
@TestPropertySource(properties = {
        "SECURITY_USER_NAME=admin",
        "SECURITY_USER_PASSWORD=admin123",
        "JWT_SECRET_KEY=test-secret-test-secret-test-secret-123456",
        "SPRING_CLOUD_VAULT_URI=http://localhost:8200",
        "VAULT_SECRET_TOKEN=test-token"
})
class ActualClientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ActualClientService service;
    @MockBean
    private VaultService vaultService;
    @MockBean
    private JwtService jwtService;
    @Test
    void addClient_shouldReturn201() throws Exception {
        DtoActualClient dto = new DtoActualClient();
        dto.setFirstName("John");
        dto.setLastName("Marston");
        dto.setCaseNumber("2356/2025");
        dto.setSubmissionDate(LocalDate.of(2025, 9, 23));
        dto.setStatus("processing");
        dto.setCompanyName("Girteka");
        dto.setRealPassword("secret123");
        dto.setEmail("john@example.com");
        ActualClient created = new ActualClient();
        created.setId(1L);
        created.setFirstName("John");
        when(service.addActualClientFromDto(any(DtoActualClient.class))).thenReturn(created);
        mockMvc.perform(post("/api/ActualClients/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }
    @Test
    void searchClients_shouldReturn200() throws Exception {
        ActualClient c = new ActualClient();
        c.setId(1L);
        c.setFirstName("Arthur");
        c.setCaseNumber("123654/2025");
        when(service.searchClients(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(c));
        mockMvc.perform(get("/api/ActualClients/search")
                        .param("firstName", "Arthur")
                        .param("caseNumber", "123654/2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
    @Test
    void getAllClients_shouldReturn200() throws Exception {
        ActualClient c1 = new ActualClient();
        c1.setId(1L);
        ActualClient c2 = new ActualClient();
        c2.setId(2L);
        when(service.getAllClientsOrThrow()).thenReturn(List.of(c1, c2));
        mockMvc.perform(get("/api/ActualClients/actual"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
    @Test
    void deleteClient_shouldReturn200() throws Exception {
        doNothing().when(service).deleteWithDependencies(1L);
        mockMvc.perform(delete("/api/ActualClients/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Client successfully deleted!"));
    }
    @Test
    void archiveClient_shouldReturn200() throws Exception {
        doNothing().when(service).archiveClient(1L);
        mockMvc.perform(post("/api/ActualClients/{id}/archive", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Client in archive"));
    }
    @Test
    void updateClient_shouldReturn200() throws Exception {
        ActualClient request = new ActualClient();
        request.setFirstName("Hosea");
        ActualClient updated = new ActualClient();
        updated.setId(1L);
        updated.setFirstName("Hosea");
        when(service.updateClient(eq(1L), any(ActualClient.class))).thenReturn(updated);
        mockMvc.perform(put("/api/ActualClients/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
    @Test
    void getClientsPaged_shouldReturn200() throws Exception {
        ActualClient c = new ActualClient();
        c.setId(1L);
        Page<ActualClient> page = new PageImpl<>(List.of(c), PageRequest.of(0, 10), 1);
        when(service.getAllClientsPaginated(any())).thenReturn(page);
        mockMvc.perform(get("/api/ActualClients/paginated")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }
    @Test
    void updateActualClientNote_shouldReturn200() throws Exception {
        DtoNote dtoNote = new DtoNote();
        dtoNote.setNote("Important note");
        dtoNote.setVersion(2L);
        ActualClient updated = new ActualClient();
        updated.setId(1L);
        updated.setNote("Important note");
        updated.setVersion(3L);
        when(service.updateClientNote(1L, "Important note",2L)).thenReturn(updated);
        mockMvc.perform(put("/api/ActualClients/{id}/notes", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoNote)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.note").value("Important note"))
                .andExpect(jsonPath("$.version").value(3));
    }
    @Test
    void getActualClientNote_shouldReturn200() throws Exception {
        ActualClient client = new ActualClient();
        client.setId(1L);
        client.setNote("Client note");
        when(service.getClientNoteOrThrow(1L)).thenReturn(client);
        mockMvc.perform(get("/api/ActualClients/actualNode/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.note").value("Client note"));
    }
    @Test
    void getActualClientsBetweenDates_shouldReturn200() throws Exception {
        ActualClient c = new ActualClient();
        c.setId(1L);
        when(service.findClientsBetweenDates(
                LocalDate.parse("2025-01-01"),
                LocalDate.parse("2025-01-31")))
                .thenReturn(List.of(c));
        mockMvc.perform(get("/api/ActualClients/complexDate")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
    @Test
    void checkClientStatus_shouldReturnHtml() throws Exception {
        DtoActualClient dto = new DtoActualClient();
        dto.setId(1L);
        dto.setCaseNumber("123/2025");
        dto.setVaultKey("client-1");
        when(service.getDtoById(1L)).thenReturn(dto);
        when(service.openStatusPageWithCredentials(dto)).thenReturn("<html>ok</html>");
        mockMvc.perform(get("/api/ActualClients/check-status/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string("<html>ok</html>"));
    }
    @Test
    void getCasePassword_shouldReturn200() throws Exception {
        DtoActualClient dto = new DtoActualClient();
        dto.setId(1L);
        dto.setVaultKey("client-1");
        when(service.getDtoById(1L)).thenReturn(dto);
        when(vaultService.getClientPassword("client-1")).thenReturn("secret123");
        mockMvc.perform(get("/api/ActualClients/realCasePassword/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("secret123"));
    }
}