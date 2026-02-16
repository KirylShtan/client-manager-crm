import clientapp.natadataservicemanagement.NataDataServiceManagementApplication;
import clientapp.natadataservicemanagement.controller.CompletedClientController;
import clientapp.natadataservicemanagement.model.CompletedClient;
import clientapp.natadataservicemanagement.security.JwtAuthenticationFilter;
import clientapp.natadataservicemanagement.security.SecurityConfig;
import clientapp.natadataservicemanagement.service.ActualClientService;
import clientapp.natadataservicemanagement.service.CompletedClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CompletedClientController.class)
@ContextConfiguration(classes = NataDataServiceManagementApplication.class)
@Import(SecurityConfig.class)
public class CompletedClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompletedClientService clientService;

    @MockBean
    private ActualClientService actualClientService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSearchClients() throws Exception {

        CompletedClient client = new CompletedClient();
        LocalDate submissionDate = LocalDate.of(2025, 1, 19);;
        client.setId(1L);
        client.setFirstName("Arthur");
        client.setLastName("Morgan");
        client.setCaseNumber("123654/2025");
        client.setSubmissionDate(submissionDate);
        client.setStatus("processing");
        client.setCompanyName("NATADATA");
        client.setPayed("yes");

        List<CompletedClient> clients = List.of(client);
        when(clientService.filterClients(anyList(), any(), any(), any(), any(), any(), any(), any(), any(),any(),any()))
                .thenReturn(clients);
        mockMvc.perform(get("/api/completed_clients/completed_search")
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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllClients() throws Exception {
        CompletedClient client = new CompletedClient();
        LocalDate submissionDate = LocalDate.of(2025, 1, 19);;
        client.setId(1L);
        client.setFirstName("Dutch");
        client.setLastName("VanDerLinde");
        client.setCaseNumber("123654354/2025");
        client.setSubmissionDate(submissionDate);
        client.setStatus("processing");
        client.setCompanyName("NATADATA");
        client.setPayed("no");

        CompletedClient client1 = new CompletedClient();
        LocalDate submissionDate1 = LocalDate.of(2025, 1, 19);;
        client1.setFirstName("Hosea");
        client1.setLastName("Matthews");
        client1.setCaseNumber("54354/2025");
        client1.setSubmissionDate(submissionDate1);
        client1.setStatus("processing");
        client1.setCompanyName("Girteka");
        client1.setPayed("no");

        List<CompletedClient> clients = List.of(client,client1);

        when(clientService.getAllClients()).thenReturn(clients);
        mockMvc.perform(get("/api/completed_clients/all_completed_clients")
                        .with(user("admin").roles("ADMIN"))
                        .contentType("application/json"))
                .andExpect(status().isOk());





    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteClient() throws Exception {
        Long clientId = 1L;

        doNothing().when(clientService).deleteClient(clientId);
        mockMvc.perform(delete("/api/completed_clients/{id}",clientId)
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());

    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateClient() throws Exception{
        CompletedClient client1 = new CompletedClient();
        Long clientId = 1L;
        LocalDate submissionDate1 = LocalDate.of(2025, 1, 19);;
        client1.setFirstName("Hosea");
        client1.setLastName("Matthews");
        client1.setCaseNumber("54354/2025");
        client1.setSubmissionDate(submissionDate1);
        client1.setStatus("processing");
        client1.setCompanyName("NATADATA");
        client1.setPayed("yes");


        when(clientService.updateClient(eq(clientId), any(CompletedClient.class)))
                .thenReturn(client1);
        mockMvc.perform(put("/api/completed_clients/{id}",clientId)
                        .content(objectMapper.writeValueAsString(client1))
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateActualClientNote() throws Exception {
        Long clientId = 1L;
        String note = "Important client note";
        CompletedClient updatedClient = new CompletedClient();
        updatedClient.setId(clientId);
        updatedClient.setFirstName("Dutch");
        updatedClient.setLastName("VanDerLinde");
        updatedClient.setNote(note);
        when(clientService.updateClientNote(clientId, note))
                .thenReturn(updatedClient);

        mockMvc.perform(put("/api/completed_clients/completedNoteUpdate/{id}", clientId)
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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getActualClientNote() throws Exception {
        Long clientId = 1L;

        CompletedClient client = new CompletedClient();
        client.setId(clientId);
        client.setNote("Client note text");

        when(clientService.getClientNote(clientId)).thenReturn(client);

        mockMvc.perform(get("/api/completed_clients/Note/{id}", clientId)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


    }


}
