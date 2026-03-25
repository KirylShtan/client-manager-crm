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
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
@TestPropertySource(properties = {"SECURITY_USER_PASSWORD=admin123"})
@TestPropertySource(properties = {"SECURITY_USER_NAME=admin"})
@TestPropertySource(properties = {"VAULT_SECRET_TOKEN=dasdfshgerg2354257y3=ffe"})
@TestPropertySource(properties = {"JWT_SECRET_KEY=daagearthwrthgerferwh5342523fw"})
@TestPropertySource(properties = {"TELEGRAM_BOT_TOKEN=563452354hrtgery4uj5yh44g4"})
@TestPropertySource(properties = {"TELEGRAM_WEBHOOK_SECRET=adwgerwtyh45tr234r3hyb4h35"})
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

        telegramService.notifyClientStatus(client.getId());



    }
}
