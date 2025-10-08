import clientapp.natadataservicemanagement.NataDataServiceManagementApplication;
import clientapp.natadataservicemanagement.controller.PositiveClientController;
import clientapp.natadataservicemanagement.model.Client;
import clientapp.natadataservicemanagement.model.PositiveClient;
import clientapp.natadataservicemanagement.security.SecurityConfig;
import clientapp.natadataservicemanagement.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = PositiveClientController.class)
@ContextConfiguration(classes = NataDataServiceManagementApplication.class)
@Import(SecurityConfig.class)
public class PositiveClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService clientService;

    @Test
    void testSearchPositiveClients() throws Exception {

        PositiveClient client = new PositiveClient();
        LocalDate submissionDate = LocalDate.now();
        client.setId(1L);
        client.setFirstName("Arthur");
        client.setLastName("Morgan");
        client.setCaseNumber("123654/2025");
        client.setSubmissionDate(submissionDate);
        client.setStatus("completed");

        List<PositiveClient> clients = List.of(client);

        List<Client> clientsAsClient = new ArrayList<>(clients);
        when(clientService.filterClients(anyList(), any(), any(), any(), any(), any(), any(),any()))
                .thenReturn(clientsAsClient);
        mockMvc.perform(get("/api/archived_positive_clients/search")
                        .param("firstName", "Arthur")
                        .param("lastName", "Morgan")
                        .param("caseNumber", "123654/2025")
                        .param("submissionDate", submissionDate.toString())
                        .param("status", "completed")
                        .with(user("user").roles("USER"))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].firstName").value("Arthur"))
                .andExpect(jsonPath("$[0].lastName").value("Morgan"))
                .andExpect(jsonPath("$[0].caseNumber").value("123654/2025"))
                .andExpect(jsonPath("$[0].submissionDate").value(submissionDate.toString()))
                .andExpect(jsonPath("$[0].status").value("completed"));

    }

    @Test
    void getAllPositiveClients() throws Exception {
        PositiveClient client = new PositiveClient();
        LocalDate submissionDate = LocalDate.now();
        client.setId(1L);
        client.setFirstName("Dutch");
        client.setLastName("VanDerLinde");
        client.setCaseNumber("123654354/2025");
        client.setSubmissionDate(submissionDate);
        client.setStatus("completed");

        PositiveClient client1 = new PositiveClient();
        LocalDate submissionDate1 = LocalDate.now();
        client1.setFirstName("Hosea");
        client1.setLastName("Matthews");
        client1.setCaseNumber("54354/2025");
        client1.setSubmissionDate(submissionDate1);
        client1.setStatus("completed");

        List<PositiveClient> clients = List.of(client, client1);

        when(clientService.getAllPositiveClients()).thenReturn(clients);
        mockMvc.perform(get("/api/archived_positive_clients/positive")
                        .with(user("admin").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Dutch"))
                .andExpect(jsonPath("$[0].lastName").value("VanDerLinde"))
                .andExpect(jsonPath("$[0].caseNumber").value("123654354/2025"))
                .andExpect(jsonPath("$[0].submissionDate").value(submissionDate.toString()))
                .andExpect(jsonPath("$[0].status").value("completed"))
                .andExpect(jsonPath("$[1].firstName").value("Hosea"))
                .andExpect(jsonPath("$[1].lastName").value("Matthews"))
                .andExpect(jsonPath("$[1].caseNumber").value("54354/2025"))
                .andExpect(jsonPath("$[1].submissionDate").value(submissionDate1.toString()))
                .andExpect(jsonPath("$[1].status").value("completed"));
    }
    @Test
    void deletePositiveClient() throws Exception {
        Long clientId = 1L;

        doNothing().when(clientService).deleteClient(clientId);
        mockMvc.perform(delete("/api/archived_positive_clients/{id}", clientId)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }
    @Test
    void updatePositiveClient() throws Exception {
        PositiveClient client1 = new PositiveClient();
        Long clientId = 1L;
        LocalDate submissionDate1 = LocalDate.now();
        client1.setFirstName("Hosea");
        client1.setLastName("Matthews");
        client1.setCaseNumber("54354/2025");
        client1.setSubmissionDate(submissionDate1);
        client1.setStatus("completed");


        when(clientService.updatePositiveClient(eq(clientId), any(PositiveClient.class)))
                .thenReturn(client1);
        mockMvc.perform(put("/api/archived_positive_clients/{id}", clientId)
                        .content(objectMapper.writeValueAsString(client1))
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Hosea"))
                .andExpect(jsonPath("$.lastName").value("Matthews"))
                .andExpect(jsonPath("$.caseNumber").value("54354/2025"))
                .andExpect(jsonPath("$.status").value("completed"))
                .andExpect(jsonPath("$.submissionDate").value(submissionDate1.toString()));


    }
    @Test
    void getAllClientsPaginated() throws Exception {
        PositiveClient client = new PositiveClient();
        LocalDate submissionDate = LocalDate.now();
        client.setId(1L);
        client.setFirstName("Arthur");
        client.setLastName("Morgan");
        client.setCaseNumber("123654/2025");
        client.setSubmissionDate(submissionDate);
        client.setStatus("completed");

        PositiveClient client1 = new PositiveClient();
        LocalDate submissionDate1 = LocalDate.now();
        client1.setFirstName("Hosea");
        client1.setLastName("Matthews");
        client1.setCaseNumber("54354/2025");
        client1.setSubmissionDate(submissionDate1);
        client1.setStatus("completed");
        client1.setId(2L);

        List<PositiveClient> clients = List.of(client,client1);

        Page<PositiveClient> clientsPage = new PageImpl<>(clients);
        when(clientService.getAllPositiveClientsPaginated(any(Pageable.class))).thenReturn(clientsPage);
        mockMvc.perform(get("/api/archived_positive_clients/pospaginated")
                        .param("page", "0")
                        .param("size", "10")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("Arthur"))
                .andExpect(jsonPath("$.content[0].lastName").value("Morgan"))
                .andExpect(jsonPath("$.content[0].caseNumber").value("123654/2025"))
                .andExpect(jsonPath("$.content[0].submissionDate").value(submissionDate.toString()))
                .andExpect(jsonPath("$.content[0].status").value("completed"))
                .andExpect(jsonPath("$.content[1].firstName").value("Hosea"))
                .andExpect(jsonPath("$.content[1].lastName").value("Matthews"))
                .andExpect(jsonPath("$.content[1].caseNumber").value("54354/2025"))
                .andExpect(jsonPath("$.content[1].submissionDate").value(submissionDate1.toString()))
                .andExpect(jsonPath("$.content[1].status").value("completed"));

    }
}
