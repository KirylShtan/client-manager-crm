import clientapp.natadataservicemanagement.NataDataServiceManagementApplication;
import clientapp.natadataservicemanagement.controller.ActualClientController;
import clientapp.natadataservicemanagement.dto.DtoActualClient;
import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.model.Client;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ActualClientController.class)
@ContextConfiguration(classes = NataDataServiceManagementApplication.class)
@Import(SecurityConfig.class)
public class ActualClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService clientService;

    @Test
    void testCreateClient() throws Exception {
        ActualClient client = new ActualClient();
        client.setFirstName("John");
        client.setLastName("Marston");
        client.setId(1L);
        client.setStatus("processing");
        client.setCaseNumber("2356/2025");
        client.setSubmissionDate(LocalDate.parse("2025-09-23"));


        when(clientService.addedActualClientFromDto(any(DtoActualClient.class))).thenReturn(client);

        mockMvc.perform(post("/api/ActualClients/add")
                        .with(user("admin").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Marston"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("processing"))
                .andExpect(jsonPath("$.submissionDate").value("2025-09-23"));

    }

    @Test
    void testSearchClients() throws Exception {

        ActualClient client = new ActualClient();
        LocalDate submissionDate = LocalDate.now();
        client.setId(1L);
        client.setFirstName("Arthur");
        client.setLastName("Morgan");
        client.setCaseNumber("123654/2025");
        client.setSubmissionDate(submissionDate);
        client.setStatus("processing");

        List<ActualClient> clients = List.of(client);

        List<Client> clientsAsClient = new ArrayList<>(clients);
        when(clientService.filterClients(anyList(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(clientsAsClient);
        mockMvc.perform(get("/api/ActualClients/search")
                        .param("firstName","Arthur")
                        .param("lastName","Morgan")
                        .param("caseNumber","123654/2025")
                        .param("submissionDate",submissionDate.toString())
                        .param("status","processing")
                .with(user("user").roles("USER"))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Arthur"))
                .andExpect(jsonPath("$[0].lastName").value("Morgan"))
                .andExpect(jsonPath("$[0].caseNumber").value("123654/2025"))
                .andExpect(jsonPath("$[0].submissionDate").value(submissionDate.toString()))
                .andExpect(jsonPath("$[0].status").value("processing"));

    }

    @Test
    void getAllClients() throws Exception {
        ActualClient client = new ActualClient();
        LocalDate submissionDate = LocalDate.now();
        client.setId(1L);
        client.setFirstName("Dutch");
        client.setLastName("VanDerLinde");
        client.setCaseNumber("123654354/2025");
        client.setSubmissionDate(submissionDate);
        client.setStatus("processing");

        ActualClient client1 = new ActualClient();
        LocalDate submissionDate1 = LocalDate.now();
        client1.setFirstName("Hosea");
        client1.setLastName("Matthews");
        client1.setCaseNumber("54354/2025");
        client1.setSubmissionDate(submissionDate1);
        client1.setStatus("processing");

        List<ActualClient> clients = List.of(client,client1);

        when(clientService.getAllClients()).thenReturn(clients);
        mockMvc.perform(get("/api/ActualClients/actual")
                .with(user("admin").roles("ADMIN"))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Dutch"))
                .andExpect(jsonPath("$[0].lastName").value("VanDerLinde"))
                .andExpect(jsonPath("$[0].caseNumber").value("123654354/2025"))
                .andExpect(jsonPath("$[0].submissionDate").value(submissionDate.toString()))
                .andExpect(jsonPath("$[0].status").value("processing"))
                .andExpect(jsonPath("$[1].firstName").value("Hosea"))
                .andExpect(jsonPath("$[1].lastName").value("Matthews"))
                .andExpect(jsonPath("$[1].caseNumber").value("54354/2025"))
                .andExpect(jsonPath("$[1].submissionDate").value(submissionDate1.toString()))
                .andExpect(jsonPath("$[1].status").value("processing"));




    }
    @Test
    void deleteClient() throws Exception {
        Long clientId = 1L;

        doNothing().when(clientService).deleteClient(clientId);
        mockMvc.perform(delete("/api/ActualClients/{id}",clientId)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());



    }
    @Test
    void archiveClient() throws Exception {
        Long clientId = 1L;
        boolean isPositive = false;

        doNothing().when(clientService).archiveClient(clientId,isPositive);
        mockMvc.perform(post("/api/ActualClients/{id}/archive",clientId)
                        .param("isPositive", String.valueOf(isPositive))
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }
    @Test
    void updateClient() throws Exception{
        ActualClient client1 = new ActualClient();
        Long clientId = 1L;
        LocalDate submissionDate1 = LocalDate.now();
        client1.setFirstName("Hosea");
        client1.setLastName("Matthews");
        client1.setCaseNumber("54354/2025");
        client1.setSubmissionDate(submissionDate1);
        client1.setStatus("processing");


        when(clientService.updateActualClient(eq(clientId), any(ActualClient.class)))
                .thenReturn(client1);
        mockMvc.perform(put("/api/ActualClients/{id}",clientId)
                        .content(objectMapper.writeValueAsString(client1))
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Hosea"))
                .andExpect(jsonPath("$.lastName").value("Matthews"))
                .andExpect(jsonPath("$.caseNumber").value("54354/2025"))
                .andExpect(jsonPath("$.status").value("processing"))
                .andExpect(jsonPath("$.submissionDate").value(submissionDate1.toString()));




    }
    @Test
    void getAllClientsPaginated() throws Exception {
        ActualClient client = new ActualClient();
        LocalDate submissionDate = LocalDate.now();
        client.setId(1L);
        client.setFirstName("Arthur");
        client.setLastName("Morgan");
        client.setCaseNumber("123654/2025");
        client.setSubmissionDate(submissionDate);
        client.setStatus("processing");

        ActualClient client1 = new ActualClient();
        LocalDate submissionDate1 = LocalDate.now();
        client1.setFirstName("Hosea");
        client1.setLastName("Matthews");
        client1.setCaseNumber("54354/2025");
        client1.setSubmissionDate(submissionDate1);
        client1.setStatus("processing");
        client1.setId(2L);

        List<ActualClient> clients = List.of(client,client1);

        Page<ActualClient> clientsPage = new PageImpl<>(clients);
        when(clientService.getAllActualClientsPaginated(any(Pageable.class))).thenReturn(clientsPage);
        mockMvc.perform(get("/api/ActualClients/paginated")
                        .param("page", "0")
                        .param("size", "10")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("Arthur"))
                .andExpect(jsonPath("$.content[0].lastName").value("Morgan"))
                .andExpect(jsonPath("$.content[0].caseNumber").value("123654/2025"))
                .andExpect(jsonPath("$.content[0].submissionDate").value(submissionDate.toString()))
                .andExpect(jsonPath("$.content[0].status").value("processing"))
                .andExpect(jsonPath("$.content[1].firstName").value("Hosea"))
                .andExpect(jsonPath("$.content[1].lastName").value("Matthews"))
                .andExpect(jsonPath("$.content[1].caseNumber").value("54354/2025"))
                .andExpect(jsonPath("$.content[1].submissionDate").value(submissionDate1.toString()))
                .andExpect(jsonPath("$.content[1].status").value("processing"));

    }
}
