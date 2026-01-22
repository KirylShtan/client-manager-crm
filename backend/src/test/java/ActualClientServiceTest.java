import clientapp.natadataservicemanagement.dto.DtoActualClient;
import clientapp.natadataservicemanagement.exception.ClientNotFoundException;
import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.model.CompletedClient;
import clientapp.natadataservicemanagement.repository.ActualClientRepository;
import clientapp.natadataservicemanagement.repository.CompletedClientRepository;
import clientapp.natadataservicemanagement.service.ActualClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActualClientServiceTest {

    @Mock
    private ActualClientRepository actualClientRepository;

    @Mock
    private CompletedClientRepository completedClientRepository;

    @InjectMocks
    private ActualClientService actualClientService;

    @Test
    void addedActualClientFromDto_savesClient() {
        DtoActualClient dto = new DtoActualClient();
        dto.setFirstName("Ivan");
        dto.setLastName("Ivanov");
        dto.setCaseNumber("123");
        dto.setSubmissionDate(LocalDate.of(2026, 1, 21));
        dto.setStatus("New");
        dto.setCompanyName("CompanyX");

        ActualClient savedClient = new ActualClient();
        savedClient.setFirstName(dto.getFirstName());

        when(actualClientRepository.save(any(ActualClient.class))).thenReturn(savedClient);

        ActualClient result = actualClientService.addedActualClientFromDto(dto);

        assertNotNull(result);
        assertEquals("Ivan", result.getFirstName());
        verify(actualClientRepository).save(any(ActualClient.class));
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



}
