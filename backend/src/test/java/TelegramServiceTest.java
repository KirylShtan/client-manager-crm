import clientapp.natadataservicemanagement.NataDataServiceManagementApplication;
import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.model.TelegramSubscriber;
import clientapp.natadataservicemanagement.repository.TelegramSubscriberRepository;
import clientapp.natadataservicemanagement.service.TelegramService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

@SpringBootTest(classes = NataDataServiceManagementApplication.class)
class TelegramServiceTest {

    @Autowired
    private TelegramService telegramService;

    @MockBean
    private TelegramSubscriberRepository subscriberRepository;

    @Test
    void testNotifyClientStatus() {
        ActualClient client = new ActualClient();
        client.setId(1L);
        TelegramSubscriber sub = new TelegramSubscriber();
        sub.setChatId(1626749962L);


        Mockito.when(subscriberRepository.findByClient_Id(1L)).thenReturn(Optional.of(sub));

        telegramService.notifyClientStatus(client);



    }
}
