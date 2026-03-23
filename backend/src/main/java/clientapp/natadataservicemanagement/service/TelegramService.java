package clientapp.natadataservicemanagement.service;

import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.model.TelegramSubscriber;
import clientapp.natadataservicemanagement.repository.ActualClientRepository;
import clientapp.natadataservicemanagement.repository.TelegramSubscriberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TelegramService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramService.class);

    @Value("${TELEGRAM_BOT_TOKEN}")
    private String botToken;

    private final TelegramSubscriberRepository telegramSubscriberRepository;
    private final RestTemplate restTemplate;
    private final ActualClientRepository actualClientRepository;

    @Autowired
    public TelegramService(TelegramSubscriberRepository telegramSubscriberRepository, RestTemplate restTemplate,
                           ActualClientRepository actualClientRepository) {
        this.telegramSubscriberRepository = telegramSubscriberRepository;
        this.restTemplate = restTemplate;
        this.actualClientRepository = actualClientRepository;
    }

    public void sendMessage(String chatId, String message) {
        if (botToken == null || botToken.isEmpty()) {
            logger.error("Telegram bot token is not set!");
            return;
        }

        if (chatId == null || chatId.isEmpty()) {
            logger.warn("Chat ID is null or empty, skipping message");
            return;
        }

        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", message);
        body.put("parse_mode", "HTML");

        try {
            logger.info("Sending Telegram message to chatId={} with body={}", chatId, body);
            String response = restTemplate.postForObject(url, body, String.class);
            logger.info("Telegram response: {}", response);
        } catch (HttpClientErrorException.Unauthorized e) {
            logger.error("Unauthorized! Check bot token or chat ID. Message not sent.", e);
        } catch (Exception e) {
            logger.error("Failed to send Telegram message", e);
        }
    }

    public void registerSubscriber(String chatId, String firstName, String lastName, ActualClient client) {
        TelegramSubscriber existingByChat = telegramSubscriberRepository.findByChatId(Long.parseLong(chatId)).orElse(null);

        if (existingByChat != null && !existingByChat.getClient().getId().equals(client.getId())) {
            throw new RuntimeException("Telegram already linked to another client");
        }

        TelegramSubscriber sub = telegramSubscriberRepository.findByClient_Id(client.getId())
                .orElse(existingByChat != null ? existingByChat : new TelegramSubscriber());

        sub.setClient(client);
        sub.setChatId(Long.parseLong(chatId));
        sub.setFirstName(firstName);
        sub.setLastName(lastName);

        telegramSubscriberRepository.save(sub);
        logger.info("Registered Telegram subscriber: chatId={}, clientId={}", chatId, client.getId());
    }

    public void notifyClientStatus(ActualClient client) {
        telegramSubscriberRepository.findByClient_Id(client.getId())
                .ifPresentOrElse(
                        subscriber -> sendMessage(
                                String.valueOf(subscriber.getChatId()),
                                "📌 <b>Status update</b>\n\nStatus: <b>" + client.getStatus() + "</b>"
                        ),
                        () -> logger.warn("Client id={} has no Telegram connection", client.getId())
                );
    }
    @Scheduled(fixedRate = 5000)
    public void pollTelegramUpdates() {

        if (botToken == null || botToken.isEmpty()) return;

        String url = "https://api.telegram.org/bot" + botToken + "/getUpdates";

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || !Boolean.TRUE.equals(response.get("ok"))) return;

            List<Map<String, Object>> updates = (List<Map<String, Object>>) response.get("result");
            if (updates == null) return;

            for (Map<String, Object> update : updates) {
                if (!update.containsKey("message")) continue;

                Map<String, Object> message = (Map<String, Object>) update.get("message");
                Map<String, Object> chat = (Map<String, Object>) message.get("chat");
                Map<String, Object> from = (Map<String, Object>) message.get("from");

                if (chat == null || from == null || message.get("text") == null) continue;
                String text = (String) message.get("text");
                if (!text.equals("/start")) continue;

                Long chatId = ((Number) chat.get("id")).longValue();
                String firstName = (String) from.get("first_name");
                String lastName = (String) from.get("last_name");


                TelegramSubscriber existing = telegramSubscriberRepository.findByChatId(chatId).orElse(null);
                if (existing != null) continue;

                ActualClient client = actualClientRepository.findTopByOrderByIdDesc()
                        .orElse(null);
                if (client == null) continue;

                boolean alreadyRegistered = telegramSubscriberRepository.findByClient_Id(client.getId()).isPresent();
                if (alreadyRegistered) continue;

                registerSubscriber(String.valueOf(chatId), firstName, lastName, client);
                sendMessage(String.valueOf(chatId), "✅ You are now connected to your case.");
            }
        } catch (Exception e) {
            logger.error("Failed to poll Telegram updates", e);
        }
    }
}
