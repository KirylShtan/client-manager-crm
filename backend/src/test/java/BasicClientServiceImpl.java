import clientapp.natadataservicemanagement.dto.DtoActualClient;
import clientapp.natadataservicemanagement.exception.ClientNotFoundException;
import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.model.CompletedClient;
import clientapp.natadataservicemanagement.repository.ActualClientRepository;
import clientapp.natadataservicemanagement.repository.CompletedClientRepository;
import clientapp.natadataservicemanagement.repository.TelegramSubscriberRepository;
import clientapp.natadataservicemanagement.service.ActualClientService;
import clientapp.natadataservicemanagement.service.VaultService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class BasicClientServiceImpl {
    @Mock
    private ActualClientRepository actualClientRepository;

    @Mock
    private CompletedClientRepository completedClientRepository;

    @InjectMocks
    private ActualClientService actualClientService;

    @Mock
    private TelegramSubscriberRepository telegramSubscriberRepository;

    @Mock
    private VaultService vaultService;

    @Test
    void addedActualClientFromDto_savesClient() {
        DtoActualClient dto = new DtoActualClient();
        dto.setFirstName("Ivan");
        dto.setLastName("Ivanov");
        dto.setCaseNumber("123");
        dto.setSubmissionDate(LocalDate.of(2026, 1, 21));
        dto.setStatus("New");
        dto.setCompanyName("CompanyX");
        dto.setRealPassword("password");

        ActualClient savedClient = new ActualClient();
        savedClient.setFirstName(dto.getFirstName());

        when(actualClientRepository.save(any(ActualClient.class))).thenReturn(savedClient);

        ActualClient result = actualClientService.addActualClientFromDto(dto);

        assertNotNull(result);
        assertEquals("Ivan", result.getFirstName());

    }

    @Test
    void archiveClient_existingClient_archivesSuccessfully() {
        Long clientId = 1L;
        ActualClient actualClient = new ActualClient();
        actualClient.setId(clientId);
        actualClient.setFirstName("Ivan");
        actualClient.setLastName("Ivanov");
        actualClient.setCaseNumber("123");
        actualClient.setStatus("InProgress");

        when(actualClientRepository.findById(clientId)).thenReturn(Optional.of(actualClient));
        when(completedClientRepository.save(any(CompletedClient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        actualClientService.archiveClient(clientId);

        verify(actualClientRepository).deleteById(clientId);
        verify(completedClientRepository).save(any(CompletedClient.class));
        assertEquals("Finished", actualClient.getStatus());
    }

    @Test
    void archiveClient_nonExistingClient_throwsException() {
        Long clientId = 99L;
        when(actualClientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> actualClientService.archiveClient(clientId));

        verify(actualClientRepository, never()).deleteById(anyLong());
        verify(completedClientRepository, never()).save(any(CompletedClient.class));
    }



    @Test
    void getAllClients_returnsList() {
        List<ActualClient> clients = List.of(new ActualClient(), new ActualClient());
        when(actualClientRepository.findAll()).thenReturn(clients);

        List<ActualClient> result = actualClientService.getAllClients();

        assertEquals(2, result.size());
        verify(actualClientRepository).findAll();
    }

    @Test
    void getAllClientsPaginated_returnsPage() {
        List<ActualClient> clients = List.of(new ActualClient(), new ActualClient());
        Page<ActualClient> page = new PageImpl<>(clients);

        when(actualClientRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<ActualClient> result = actualClientService.getAllClientsPaginated(Pageable.unpaged());

        assertEquals(2, result.getContent().size());
        verify(actualClientRepository).findAll(any(Pageable.class));
    }

    @Test
    void updateClient_existingClient_updatesCorrectly() {
        Long id = 1L;
        ActualClient client = new ActualClient();
        client.setId(id);
        client.setFirstName("Ivan");
        client.setCaseNumber("123");

        ActualClient updatedClient = new ActualClient();
        updatedClient.setFirstName("Petr");
        updatedClient.setCaseNumber("456");

        when(actualClientRepository.findById(id)).thenReturn(Optional.of(client));
        when(actualClientRepository.save(any(ActualClient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ActualClient result = actualClientService.updateClient(id, updatedClient);

        assertEquals("Petr", result.getFirstName());
        assertEquals("456", result.getCaseNumber());
        verify(actualClientRepository).save(client);
    }

    @Test
    void updateClient_nonExistingClient_throwsException() {
        Long id = 99L;
        ActualClient client = new ActualClient();

        when(actualClientRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> actualClientService.updateClient(id, client));
        verify(actualClientRepository, never()).save(any());
    }

    @Test
    void filterClients_filtersCorrectly() {
        ActualClient client1 = new ActualClient();
        client1.setFirstName("Ivan");
        client1.setLastName("Petrov");
        client1.setCaseNumber("123");
        client1.setStatus("New");
        client1.setCompanyName("CompanyX");
        client1.setSubmissionDate(LocalDate.of(2026,1,21));

        ActualClient client2 = new ActualClient();
        client2.setFirstName("Petr");
        client2.setLastName("Ivanov");
        client2.setCaseNumber("456");
        client2.setStatus("Finished");
        client2.setCompanyName("CompanyY");
        client2.setSubmissionDate(LocalDate.of(2026,1,20));

        List<ActualClient> clients = List.of(client1, client2);

        List<ActualClient> filtered = actualClientService.filterClients(
                clients,
                null,
                "Ivan",
                "Ivanov",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertEquals(2, filtered.size());
    }

    @Test
    void findBetweenDates_filtersByDateCorrectly() {
        ActualClient client1 = new ActualClient();
        client1.setSubmissionDate(LocalDate.of(2026,1,21));

        ActualClient client2 = new ActualClient();
        client2.setSubmissionDate(LocalDate.of(2026,1,19));

        List<ActualClient> clients = List.of(client1, client2);

        List<ActualClient> filtered = actualClientService.findBetweenDates(clients, "2026-01-20", "2026-01-22");

        assertEquals(1, filtered.size());
        assertEquals(client1, filtered.get(0));
    }

    @Test
    void getClientNote_existingClient_returnsClient() {
        ActualClient client = new ActualClient();
        client.setId(1L);

        when(actualClientRepository.findById(1L)).thenReturn(Optional.of(client));

        ActualClient result = actualClientService.getClientNote(1L);

        assertEquals(client, result);
    }

    @Test
    void getClientNote_nonExistingClient_throwsException() {
        when(actualClientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> actualClientService.getClientNote(1L));
    }

    @Test
    void updateClientNote_existingClient_updatesNote() {
        ActualClient client = new ActualClient();
        client.setId(1L);
        client.setNote("Old note");

        when(actualClientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(actualClientRepository.save(client)).thenAnswer(invocation -> invocation.getArgument(0));

        ActualClient result = actualClientService.updateClientNote(1L, "New note");

        assertEquals("New note", result.getNote());
        verify(actualClientRepository).save(client);
    }

    @Test
    void updateClientNote_nonExistingClient_throwsException() {
        when(actualClientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> actualClientService.updateClientNote(1L, "New note"));
    }

}
