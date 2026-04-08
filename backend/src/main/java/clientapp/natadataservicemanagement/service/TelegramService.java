package clientapp.natadataservicemanagement.service;

import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.model.TelegramSubscriber;
import clientapp.natadataservicemanagement.repository.ActualClientRepository;
import clientapp.natadataservicemanagement.repository.TelegramSubscriberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TelegramService {

    private long lastUpdateId = 0;

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

    public ResponseEntity<String> registerSubscriber(String chatId, String firstName, String lastName, Long clientId) {
        return actualClientRepository.findById(clientId)
                .map(client -> {
                    TelegramSubscriber existingByChat = telegramSubscriberRepository.findByChatId(Long.parseLong(chatId))
                            .orElse(null);

                    if (existingByChat != null && !existingByChat.getClient().getId().equals(clientId)) {
                        return ResponseEntity.badRequest().body("Telegram already linked to another client");
                    }

                    TelegramSubscriber subscriber = telegramSubscriberRepository.findByClient_Id(clientId)
                            .orElse(existingByChat != null ? existingByChat : new TelegramSubscriber());

                    subscriber.setClient(client);
                    subscriber.setChatId(Long.parseLong(chatId));
                    subscriber.setFirstName(firstName);
                    subscriber.setLastName(lastName);

                    telegramSubscriberRepository.save(subscriber);
                    logger.info("Registered Telegram subscriber: chatId={}, clientId={}", chatId, clientId);
                    return ResponseEntity.ok("Subscriber registered successfully");
                })
                .orElseGet(() -> ResponseEntity.badRequest().body("Client not found"));
    }

    public String notifyClientStatus(Long clientId) {
        actualClientRepository.findById(clientId).ifPresentOrElse(
                client -> telegramSubscriberRepository.findByClient_Id(clientId)
                        .ifPresentOrElse(
                                subscriber -> sendMessage(
                                        String.valueOf(subscriber.getChatId()),
                                        "📌 <b>Status update</b>\n\nStatus: <b>" + client.getStatus() + "</b>"
                                ),
                                () -> logger.warn("Client id={} has no Telegram connection", clientId)
                        ),
                () -> logger.warn("Client id={} not found", clientId)
        );

        return "Notification sent!";
    }
    @Scheduled(fixedRate = 5000)
    public void pollTelegramUpdates() {
        if (botToken == null || botToken.isEmpty()) return;

        // Первый раз: получаем все старые апдейты и игнорируем их
        if (lastUpdateId == 0) {
            try {
                Map<String, Object> initialResponse = restTemplate.getForObject(
                        "https://api.telegram.org/bot" + botToken + "/getUpdates", Map.class
                );
                if (initialResponse != null && Boolean.TRUE.equals(initialResponse.get("ok"))) {
                    List<Map<String, Object>> updates = (List<Map<String, Object>>) initialResponse.get("result");
                    if (updates != null && !updates.isEmpty()) {
                        lastUpdateId = ((Number) updates.get(updates.size() - 1).get("update_id")).longValue();
                        // Теперь Telegram больше не вернет эти старые апдейты
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to clear old Telegram updates: {}", e.getMessage());
            }
        }


        String url = "https://api.telegram.org/bot" + botToken + "/getUpdates?offset=" + (lastUpdateId + 1);
        Map<String, Object> response;

        try {
            response = restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            logger.error("Failed to fetch updates from Telegram: {}", e.getMessage());
            return;
        }

        if (response == null || !Boolean.TRUE.equals(response.get("ok"))) return;

        List<Map<String, Object>> updates = (List<Map<String, Object>>) response.get("result");
        if (updates == null) return;

        for (Map<String, Object> update : updates) {
            try {
                lastUpdateId = ((Number) update.get("update_id")).longValue();

                Map<String, Object> message = (Map<String, Object>) update.get("message");
                if (message == null) continue;

                Map<String, Object> chat = (Map<String, Object>) message.get("chat");
                Map<String, Object> from = (Map<String, Object>) message.get("from");
                String text = (String) message.get("text");

                if (chat == null || from == null || text == null || !text.startsWith("/start")) continue;

                Long chatId = ((Number) chat.get("id")).longValue();
                String firstName = (String) from.get("first_name");
                String lastName = (String) from.get("last_name");

                Optional<ActualClient> lastClient = actualClientRepository.findTopByOrderByIdDesc();
                if (lastClient.isEmpty()) continue;

                Long clientId = lastClient.get().getId();

                registerSubscriber(String.valueOf(chatId), firstName, lastName, clientId);
                sendMessage(String.valueOf(chatId), "✅ You are now connected to your case.");

            } catch (Exception e) {
                logger.warn("Skipping update due to error: {}", e.getMessage());
            }
        }
    }
    public ResponseEntity<String> processTelegramUpdate(Map<String, Object> update) {

        if (!update.containsKey("message")) return ResponseEntity.ok("No message");

        Map<String, Object> message = (Map<String, Object>) update.get("message");
        Map<String, Object> chat = (Map<String, Object>) message.get("chat");
        Map<String, Object> from = (Map<String, Object>) message.get("from");
        String text = (String) message.get("text");

        if (chat == null || from == null || text == null || !text.startsWith("/start"))
            return ResponseEntity.ok("Not a start command");

        String chatId = String.valueOf(chat.get("id"));
        String firstName = (String) from.get("first_name");
        String lastName = (String) from.get("last_name");

        String[] parts = text.split(" ");
        if (parts.length < 2) {
            sendMessage(chatId, "Invalid link.");
            return ResponseEntity.ok("Invalid link");
        }

        Long clientId;
        try {
            clientId = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Invalid client ID.");
            return ResponseEntity.badRequest().body("Invalid client ID");
        }
        Optional<ActualClient> clientOpt = actualClientRepository.findById(clientId);
        if (clientOpt.isEmpty()) {
            sendMessage(chatId, "Client does not exist.");

            telegramSubscriberRepository.findByChatId(Long.parseLong(chatId))
                    .ifPresent(telegramSubscriberRepository::delete);
            return ResponseEntity.badRequest().body("Client not found");
        }

        ResponseEntity<String> response = registerSubscriber(chatId, firstName, lastName, clientId);


        if (response.getStatusCode().is2xxSuccessful()) {
            sendMessage(chatId, "✅ You are successfully connected to your case.");
        }

        return response;
    }
}
