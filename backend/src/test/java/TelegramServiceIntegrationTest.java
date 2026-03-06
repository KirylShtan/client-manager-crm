import clientapp.natadataservicemanagement.NataDataServiceManagementApplication;
import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.model.TelegramSubscriber;
import clientapp.natadataservicemanagement.repository.ActualClientRepository;
import clientapp.natadataservicemanagement.repository.TelegramSubscriberRepository;
import clientapp.natadataservicemanagement.service.TelegramService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


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


        telegramService.notifyClientStatus(savedClient);
        subscriberRepository.delete(subscriber);
        actualClientRepository.delete(actualClient);

        System.out.println("Message should appear in your Telegram chat!");
    }
    @Test
    void cleanTestData() {
        subscriberRepository.findById(1626749962L).ifPresent(subscriberRepository::delete);

    }
}
