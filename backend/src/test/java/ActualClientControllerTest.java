import clientapp.natadataservicemanagement.NataDataServiceManagementApplication;
import clientapp.natadataservicemanagement.controller.ActualClientController;
import clientapp.natadataservicemanagement.dto.DtoActualClient;
import clientapp.natadataservicemanagement.exception.ClientNotFoundException;
import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.model.Client;
import clientapp.natadataservicemanagement.security.JwtAuthenticationFilter;
import clientapp.natadataservicemanagement.security.SecurityConfig;
import clientapp.natadataservicemanagement.service.ActualClientService;
import clientapp.natadataservicemanagement.service.CompletedClientService;
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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ActualClientController.class)
@ContextConfiguration(classes = NataDataServiceManagementApplication.class)
@Import(SecurityConfig.class)
public class ActualClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ActualClientService clientService;

    @MockBean
    private CompletedClientService completedClientService;

    @MockBean
    private JwtAuthenticationFilter authenticationFilter;

    @Test
    void testCreateClient() throws Exception {
        ActualClient client = new ActualClient();
        client.setFirstName("John");
        client.setLastName("Marston");
        client.setId(1L);
        client.setStatus("processing");
        client.setCaseNumber("2356/2025");
        client.setSubmissionDate(LocalDate.parse("2025-09-23"));
        client.setArchiveDate(LocalDate.now());
        client.setNote("note");
        client.setCompanyName("Girteka");
        client.setPayed("yes");


        when(clientService.addedActualClientFromDto(any(DtoActualClient.class)).thenReturn(client));

        mockMvc.perform(post("/api/ActualClients/add")
                        .with(user("admin").roles("ADMIN"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isOk());


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
        client.setCompanyName("NATADATA");
        client.setPayed("yes");

        List<ActualClient> clients = List.of(client);

        List<Client> clientsAsClient = new ArrayList<>(clients);
        when(clientService.filterClients(anyList(), any(), any(), any(), any(), any(), any(), any(), any(),any(),any()))
                .thenReturn(clients);
        mockMvc.perform(get("/api/ActualClients/search")
                        .param("firstName","Arthur")
                        .param("lastName","Morgan")
                        .param("caseNumber","123654/2025")
                        .param("submissionDate",submissionDate.toString())
                        .param("status","processing")
                        .param("companyName","NATADATA")
                        .param("payed","yes")
                .with(user("user").roles("USER"))
                        .contentType("application/json"))
                .andExpect(status().isOk());

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
        client.setCompanyName("NATADATA");
        client.setPayed("no");

        ActualClient client1 = new ActualClient();
        LocalDate submissionDate1 = LocalDate.now();
        client1.setFirstName("Hosea");
        client1.setLastName("Matthews");
        client1.setCaseNumber("54354/2025");
        client1.setSubmissionDate(submissionDate1);
        client1.setStatus("processing");
        client1.setCompanyName("Girteka");
        client1.setPayed("no");

        List<ActualClient> clients = List.of(client,client1);

        when(clientService.getAllClients()).thenReturn(clients);
        mockMvc.perform(get("/api/ActualClients/actual")
                .with(user("admin").roles("ADMIN"))
                .contentType("application/json"))
                .andExpect(status().isOk());





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

        doNothing().when(clientService).archiveClient(clientId);
        mockMvc.perform(post("/api/ActualClients/{id}/archive",clientId)
                        .param("isPositive", String.valueOf(isPositive))
                .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }
    @Test
    void updateClient() throws Exception {
        Long clientId = 1L;
        ActualClient client1 = new ActualClient();
        client1.setId(clientId); // обязательно выставляем ID
        client1.setFirstName("Hosea");
        client1.setLastName("Matthews");
        client1.setCaseNumber("54354/2025");
        client1.setSubmissionDate(LocalDate.now());
        client1.setStatus("processing");
        client1.setCompanyName("NATADATA");
        client1.setPayed("yes");

        when(clientService.updateClient(eq(clientId), any(ActualClient.class)))
                .thenReturn(client1);

        mockMvc.perform(put("/api/ActualClients/{id}", clientId)
                        .content(objectMapper.writeValueAsString(client1))
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


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
        client.setCompanyName("Girteka");
        client.setPayed("no");


        ActualClient client1 = new ActualClient();
        LocalDate submissionDate1 = LocalDate.now();
        client1.setFirstName("Hosea");
        client1.setLastName("Matthews");
        client1.setCaseNumber("54354/2025");
        client1.setSubmissionDate(submissionDate1);
        client1.setStatus("processing");
        client1.setId(2L);
        client1.setCompanyName("NATADATA");
        client1.setPayed("yes");


        List<ActualClient> clients = List.of(client,client1);

        Page<ActualClient> clientsPage = new PageImpl<>(clients);
        when(clientService.getAllClientsPaginated(any(Pageable.class))).thenReturn(clientsPage);
        mockMvc.perform(get("/api/ActualClients/paginated")
                        .param("page", "0")
                        .param("size", "10")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());



    }
    @Test
    void updateActualClientNote() throws Exception {
        Long clientId = 1L;
        String note = "note";
        ActualClient updatedClient = new ActualClient();
        updatedClient.setId(clientId);
        updatedClient.setFirstName("Dutch");
        updatedClient.setLastName("VanDerLinde");
        updatedClient.setNote(note);
        when(clientService.updateClientNote(clientId, note))
                .thenReturn(updatedClient);

        mockMvc.perform(put("/api/ActualClients/{id}/notes", clientId)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "note": "Important client note"
                        }
                        """))
                .andExpect(status().isOk());

    }
    @Test
    void getActualClientNote() throws Exception {
        Long clientId = 1L;

        ActualClient client = new ActualClient();
        client.setId(clientId);
        client.setNote("Client note text");

        when(clientService.getClientNote(clientId)).thenReturn(client);

        mockMvc.perform(get("/api/ActualClients/actualNode/{id}", clientId)
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void getActualClientsBetweenDates_shouldReturnClients() throws Exception {
        List<ActualClient> allClients = List.of(
                clientWithDate(1L, LocalDate.of(2025, 1, 10)),
                clientWithDate(2L, LocalDate.of(2025, 1, 20))
        );

        List<ActualClient> filteredClients = List.of(
                allClients.get(0)
        );

        when(clientService.getAllClients()).thenReturn(allClients);
        when(clientService.findBetweenDates(
                allClients, "2025-01-01", "2025-01-15"))
                .thenReturn(filteredClients);

        mockMvc.perform(get("/api/ActualClients/complexDate")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-15")
                        .with(user("user").roles("USER")))
                .andExpect(status().isOk());
    }


    @Test
    void getActualClientsBetweenDates_shouldReturn400OnInvalidDate() throws Exception {
        List<ActualClient> clients = List.of(new ActualClient());

        when(clientService.getAllClients()).thenReturn(clients);
        when(clientService.findBetweenDates(any(), any(), any()))
                .thenThrow(new DateTimeParseException("Invalid date", "wrong", 0));

        mockMvc.perform(get("/api/ActualClients/complexDate")
                        .param("startDate", "wrong")
                        .param("endDate", "2025-01-10")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());


    }
    @Test
    void getActualClientsBetweenDates_shouldReturn500WhenClientsNotFound() throws Exception {
        when(clientService.getAllClients())
                .thenThrow(new ClientNotFoundException("Repo empty"));

        mockMvc.perform(get("/api/ActualClients/complexDate")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-10")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());

    }
    private ActualClient clientWithDate(Long id, LocalDate date) {
        ActualClient client = new ActualClient();
        client.setId(id);
        client.setSubmissionDate(date);
        return client;
    }



}

