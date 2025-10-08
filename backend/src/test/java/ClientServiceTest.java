import clientapp.natadataservicemanagement.dto.DtoActualClient;
import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.model.Client;
import clientapp.natadataservicemanagement.model.NegativeClient;
import clientapp.natadataservicemanagement.model.PositiveClient;
import clientapp.natadataservicemanagement.repository.ClientRepository;
import clientapp.natadataservicemanagement.repository.NegativeClientRepository;
import clientapp.natadataservicemanagement.repository.PositiveClientRepository;
import clientapp.natadataservicemanagement.service.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private NegativeClientRepository negativeClientRepository;
    @Mock
    private PositiveClientRepository positiveClientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    void updateActualClient() {
        ActualClient client = new ActualClient();
        Long clientId = 1L;
        LocalDate submissionDate = LocalDate.now();
        client.setFirstName("Hosea");
        client.setLastName("Matthews");
        client.setCaseNumber("54354/2025");
        client.setSubmissionDate(submissionDate);
        client.setStatus("processing");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(ActualClient.class))).thenReturn(client);

        ActualClient updated = clientService.updateActualClient(clientId, client);
        assertEquals("Hosea", updated.getFirstName());
        assertEquals("Matthews", updated.getLastName());
        assertEquals("54354/2025", updated.getCaseNumber());
        assertEquals("processing", updated.getStatus());
        assertEquals(submissionDate, updated.getSubmissionDate());

    }

    @Test
    void updateNegativeClient() {
        NegativeClient client = new NegativeClient();
        Long clientId = 1L;
        LocalDate submissionDate = LocalDate.now();
        client.setFirstName("Hosea");
        client.setLastName("Matthews");
        client.setCaseNumber("54354/2025");
        client.setSubmissionDate(submissionDate);
        client.setStatus("processing");

        when(negativeClientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(negativeClientRepository.save(any(NegativeClient.class))).thenReturn(client);

        NegativeClient updated = clientService.updateNegativeClient(clientId, client);
        assertEquals("Hosea", updated.getFirstName());
        assertEquals("Matthews", updated.getLastName());
        assertEquals("54354/2025", updated.getCaseNumber());
        assertEquals("processing", updated.getStatus());
        assertEquals(submissionDate, updated.getSubmissionDate());

    }

    @Test
    void updatePositiveClient() {
        PositiveClient existing = new PositiveClient();
        LocalDate submissionDate = LocalDate.now();
        Long clientId = 1L;
        existing.setFirstName("Hosea");
        existing.setLastName("Matthews");
        existing.setCaseNumber("54354/2025");
        existing.setSubmissionDate(submissionDate);
        existing.setStatus("processing");

        PositiveClient updated = new PositiveClient();
        updated.setFirstName("Hosea");
        updated.setLastName("Matthews");
        updated.setCaseNumber("54354/2025");
        updated.setSubmissionDate(submissionDate);
        updated.setStatus("finished");

        when(positiveClientRepository.findById(clientId)).thenReturn(Optional.of(existing));
        when(positiveClientRepository.save(any(PositiveClient.class))).thenReturn(updated);

        PositiveClient completed = clientService.updatePositiveClient(clientId, updated);
        System.out.println(completed);
        assertEquals("Hosea", completed.getFirstName());
        assertEquals("Matthews", completed.getLastName());
        assertEquals("54354/2025", completed.getCaseNumber());
        assertEquals("finished", completed.getStatus());
        assertEquals(submissionDate, completed.getSubmissionDate());


    }
    @Test
    void archiveTest(){
        ActualClient client = new ActualClient();
        boolean isPositive = false;
        Long clientId = 1L;
        client.setFirstName("Arthur");
        client.setLastName("Morgan");
        client.setSubmissionDate(LocalDate.now());
        client.setStatus("completed");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(negativeClientRepository.save(any(NegativeClient.class))).thenReturn(new NegativeClient());

        clientService.archiveClient(clientId,isPositive);
        System.out.println(client.getArchiveDate());
        ArgumentCaptor<NegativeClient> captor = ArgumentCaptor.forClass(NegativeClient.class);
        verify(negativeClientRepository).save(captor.capture());
        System.out.println(captor.getValue().getArchiveDate());

    }
    @Test
    void deleteActualClient(){
        Long clientId = 1L;
        when(clientRepository.existsById(clientId)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(clientId);
        clientService.deleteClient(clientId);
        verify(clientRepository).deleteById(clientId);
    }

    @Test
    void deleteNegativeClient(){
        Long clientId = 3L;
        when(negativeClientRepository.existsById(clientId)).thenReturn(true);
        doNothing().when(negativeClientRepository).deleteById(clientId);
        clientService.deleteNegativeClient(clientId);
        verify(negativeClientRepository).deleteById(clientId);


    }

    @Test
    void deletePositiveClient(){
        Long clientId = 2L;
        when(positiveClientRepository.existsById(clientId)).thenReturn(true);
        doNothing().when(positiveClientRepository).deleteById(clientId);
        clientService.deletePositiveClient(clientId);
        verify(positiveClientRepository).deleteById(clientId);


    }
    @Test
    void filterClientsTest(){
        ActualClient client = new ActualClient();
        Long clientId = 1L;
        LocalDate submissionDate = LocalDate.now();
        client.setFirstName("John");
        client.setLastName("Marston");
        client.setSubmissionDate(submissionDate);
        client.setCaseNumber("55555/2025");
        client.setStatus("processing");

        lenient().when(clientRepository.findAll()).thenReturn(List.of(client));
        List<Client> filteredClients = clientService.filterClients(
                List.of(client), null, null, null,
                null, null, null, null
        );
        assertEquals(1, filteredClients.size());
        assertEquals("John", filteredClients.get(0).getFirstName());
        assertEquals("Marston", filteredClients.get(0).getLastName());
        assertEquals(submissionDate, filteredClients.get(0).getSubmissionDate());
        assertEquals("55555/2025",filteredClients.get(0).getCaseNumber());
        assertEquals("processing",filteredClients.get(0).getStatus());
    }
    @Test
    void addedActualClientFromDto() {
        DtoActualClient dto = new DtoActualClient();
        dto.setFirstName("John");
        dto.setLastName("Marston");
        LocalDate submissionDate = LocalDate.now();
        dto.setSubmissionDate(submissionDate);
        dto.setCaseNumber("55552342342/2025");
        dto.setStatus("processing");

        // Мокаем репозиторий: возвращаем объект, который передали
        when(clientRepository.save(any(ActualClient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Вызываем сервис, который должен сохранить клиента
        ActualClient client = clientService.addedActualClientFromDto(dto);

        // Проверяем, что сервис правильно скопировал данные из DTO
        assertEquals("John", client.getFirstName());
        assertEquals("Marston", client.getLastName());
        assertEquals(submissionDate, client.getSubmissionDate());
        assertEquals("55552342342/2025", client.getCaseNumber());
        assertEquals("processing", client.getStatus());
    }

}
