package clientapp.natadataservicemanagement.controller;

import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.repository.ActualClientRepository;
import clientapp.natadataservicemanagement.repository.TelegramSubscriberRepository;
import clientapp.natadataservicemanagement.service.TelegramService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("api/telegram")
public class TelegramController {

    private final TelegramService telegramService;
    private final ActualClientRepository actualClientRepository;

    @PostMapping("/update")
    public void receiveUpdate(@RequestBody Map<String, Object> update) {

        if (update.containsKey("message")) {
            Map<String,Object> message = (Map<String, Object>) update.get("message");
            Map<String, Object> from = (Map<String, Object>) message.get("from");
            Map<String, Object> chat = (Map<String, Object>) message.get("chat");

            Long chatId = ((Number) chat.get("id")).longValue();
            String firstName = (String) from.get("first_name");
            String lastName = (String) from.get("last_name");
            Long clientId = 123L;

            ActualClient client = actualClientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            telegramService.registerSubscriber(chatId, firstName, lastName, client);
        }
    }

    @PostMapping("/notifyStatus")
    public void notifyStatus(@RequestParam Long clientId) {
        ActualClient client = actualClientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        telegramService.notifyClientStatus(client);
    }
}
