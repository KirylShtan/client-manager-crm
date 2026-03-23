package clientapp.natadataservicemanagement.controller;

import clientapp.natadataservicemanagement.model.ActualClient;
import clientapp.natadataservicemanagement.model.TelegramSubscriber;
import clientapp.natadataservicemanagement.repository.ActualClientRepository;
import clientapp.natadataservicemanagement.service.TelegramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/telegram")
public class TelegramController {

    private static final Logger logger = LoggerFactory.getLogger(TelegramController.class);

    private final TelegramService telegramService;
    private final ActualClientRepository actualClientRepository;

    @Value("${telegram.webhook.secret}")
    private String webhookSecret;

    public TelegramController(TelegramService telegramService, ActualClientRepository actualClientRepository) {
        this.telegramService = telegramService;
        this.actualClientRepository = actualClientRepository;
    }
    @Operation(summary = "receiving update",
               description = "receiving update with Telegram ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "all updates are up to date", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TelegramSubscriber.class)
            )),
            @ApiResponse(responseCode = "403",description = "Unauthorized attempt", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "500", description = "Unexpected behaviour", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/update")
    public ResponseEntity<String> receiveUpdate(
            @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false) String secret,
            @RequestBody Map<String, Object> update) {

        if (secret == null || !secret.equals(webhookSecret)) {
            logger.warn("Unauthorized Telegram webhook attempt");
            return ResponseEntity.status(403).body("Forbidden");
        }

        if (!update.containsKey("message")) return ResponseEntity.ok("No message");

        Map<String, Object> message = (Map<String, Object>) update.get("message");
        if (message.get("chat") == null || message.get("from") == null) return ResponseEntity.ok("No chat or from info");

        Map<String, Object> chat = (Map<String, Object>) message.get("chat");
        Map<String, Object> from = (Map<String, Object>) message.get("from");

        String chatId = String.valueOf(chat.get("id"));
        String firstName = (String) from.get("first_name");
        String lastName = (String) from.get("last_name");
        String text = (String) message.get("text");

        if (text == null || !text.startsWith("/start")) return ResponseEntity.ok("Not a start command");

        String[] parts = text.split(" ");
        if (parts.length < 2) {
            telegramService.sendMessage(chatId, "Invalid link.");
            return ResponseEntity.ok("Invalid link");
        }

        try {
            Long clientId = Long.parseLong(parts[1]);
            ActualClient client = actualClientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            telegramService.registerSubscriber(chatId, firstName, lastName, client);
            telegramService.sendMessage(chatId, "✅ You are successfully connected to your case.");
            return ResponseEntity.ok("Subscriber registered");

        } catch (NumberFormatException e) {
            telegramService.sendMessage(chatId, "Invalid client ID.");
            return ResponseEntity.badRequest().body("Invalid client ID");
        } catch (RuntimeException e) {
            telegramService.sendMessage(chatId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Operation(summary = "notifying attempt",
            description = "notifying by using clientId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "notification ends successfully", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TelegramSubscriber.class)
            )),
            @ApiResponse(responseCode = "403",description = "Unauthorized attempt", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "500", description = "Unexpected behaviour", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/notifyStatus")
    public ResponseEntity<String> notifyStatus(@RequestParam Long clientId) {
        try {
            ActualClient client = actualClientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            telegramService.notifyClientStatus(client);
            return ResponseEntity.ok("Notification sent!");
        } catch (RuntimeException e) {
            logger.warn("Failed to notify client id={}: {}", clientId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Operation(summary = "registering client ",
            description = "registering client client by using firstName, lastName ,chatId, clientId ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "registration ends successfully", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TelegramSubscriber.class)
            )),
            @ApiResponse(responseCode = "403",description = "Unauthorized attempt", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )),
            @ApiResponse(responseCode = "500", description = "Unexpected behaviour", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class)
            ))
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/registerSubscriber")
    public ResponseEntity<String> registerSubscriber(
            @RequestParam Long clientId,
            @RequestParam String chatId,
            @RequestParam String firstName,
            @RequestParam String lastName) {

        try {
            ActualClient client = actualClientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            telegramService.registerSubscriber(chatId, firstName, lastName, client);

            return ResponseEntity.ok("Subscriber registered successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
