package clientapp.natadataservicemanagement.service;

import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.model.TelegramSubscriber;
import clientapp.natadataservicemanagement.repository.TelegramSubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final TelegramSubscriberRepository telegramSubscriberRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public TelegramService(TelegramSubscriberRepository telegramSubscriberRepository, RestTemplate restTemplate) {
        this.telegramSubscriberRepository = telegramSubscriberRepository;
        this.restTemplate = restTemplate;
    }

    public void sendMessage(Long chatId, String message) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        Map<String,Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", message);
        body.put("parse_mode", "HTML");

        try {
            restTemplate.postForObject(url, body, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerSubscriber(Long chatId, String firstName, String lastName, ActualClient client) {
        if (!telegramSubscriberRepository.existsById(chatId)) {
            TelegramSubscriber sub = new TelegramSubscriber();
            sub.setChatId(chatId);
            sub.setClient(client);
            telegramSubscriberRepository.save(sub);
        }
    }

    public void notifyClientStatus(ActualClient client) {
        telegramSubscriberRepository.findByClient_Id(client.getId())
                .ifPresent(subscriber -> sendMessage(subscriber.getChatId(),
                        "Your case status: " + client.getStatus()));
    }
}





