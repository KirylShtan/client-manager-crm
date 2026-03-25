import clientapp.natadataservicemanagement.NataDataServiceManagementApplication;
import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.model.TelegramSubscriber;
import clientapp.natadataservicemanagement.repository.ActualClientRepository;
import clientapp.natadataservicemanagement.repository.TelegramSubscriberRepository;
import clientapp.natadataservicemanagement.service.TelegramService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"SECURITY_USER_PASSWORD=admin123"})
@TestPropertySource(properties = {"SECURITY_USER_NAME=admin"})
@TestPropertySource(properties = {"VAULT_SECRET_TOKEN=dasdfshgerg2354257y3=ffe"})
@TestPropertySource(properties = {"JWT_SECRET_KEY=daagearthwrthgerferwh5342523fw"})
@TestPropertySource(properties = {"TELEGRAM_BOT_TOKEN=563452354hrtgery4uj5yh44g4"})
@TestPropertySource(properties = {"TELEGRAM_WEBHOOK_SECRET=adwgerwtyh45tr234r3hyb4h35"})
@SpringBootTest(classes = NataDataServiceManagementApplication.class)
public class TelegramServiceIntegrationTest {
    @Autowired
    private TelegramService telegramService;
    @Autowired
    private TelegramSubscriberRepository subscriberRepository;
    @Autowired
    private ActualClientRepository actualClientRepository;

    @Test
    void sendMessageToTelegram() {
        Long chatId = 5446371602L;
        ActualClient actualClient = new ActualClient();
        actualClient.setStatus("Test client");


        final ActualClient savedClient = actualClientRepository.save(actualClient);


        TelegramSubscriber subscriber = subscriberRepository.findById(chatId)
                .orElseGet(() -> {
                    TelegramSubscriber sub = new TelegramSubscriber();
                    sub.setChatId(chatId);
                    sub.setClient(savedClient);
                    return subscriberRepository.save(sub);
                });


        telegramService.notifyClientStatus(savedClient.getId());
        subscriberRepository.delete(subscriber);
        actualClientRepository.delete(actualClient);

        System.out.println("Message should appear in your Telegram chat!");
    }
    @Test
    void cleanTestData() {
        subscriberRepository.findById(1626749962L).ifPresent(subscriberRepository::delete);

    }
}
