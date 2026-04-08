package clientapp.natadataservicemanagement.service;
import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.model.TelegramSubscriber;
import clientapp.natadataservicemanagement.repository.ActualClientRepository;
import clientapp.natadataservicemanagement.repository.TelegramSubscriberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class TelegramServiceTest {
    @Mock
    private TelegramSubscriberRepository subscriberRepository;
    @Mock
    private ActualClientRepository actualClientRepository;
    @Mock
    private RestTemplate restTemplate;
    @Spy
    @InjectMocks
    private TelegramService telegramService;
    @Test
    void notifyClientStatus_shouldSendMessage_whenClientAndSubscriberExist() {
        ActualClient client = new ActualClient();
        client.setId(1L);
        client.setStatus("processing");
        TelegramSubscriber subscriber = new TelegramSubscriber();
        subscriber.setChatId(1626749962L);
        subscriber.setClient(client);
        when(actualClientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(subscriberRepository.findByClient_Id(1L)).thenReturn(Optional.of(subscriber));
        telegramService.notifyClientStatus(1L);
        verify(telegramService).sendMessage(
                "1626749962",
                "📌 <b>Status update</b>\n\nStatus: <b>processing</b>"
        );
    }
    @Test
    void notifyClientStatus_shouldNotSendMessage_whenClientMissing() {
        when(actualClientRepository.findById(1L)).thenReturn(Optional.empty());
        telegramService.notifyClientStatus(1L);
        verify(telegramService, never()).sendMessage(anyString(), anyString());
    }
    @Test
    void notifyClientStatus_shouldNotSendMessage_whenSubscriberMissing() {
        ActualClient client = new ActualClient();
        client.setId(1L);
        client.setStatus("processing");
        when(actualClientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(subscriberRepository.findByClient_Id(1L)).thenReturn(Optional.empty());
        telegramService.notifyClientStatus(1L);
        verify(telegramService, never()).sendMessage(anyString(), anyString());
    }
}