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
            @ApiResponse(responseCode = "403", description = "Unauthorized attempt", content = @Content(
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
        return telegramService.processTelegramUpdate(update);
    }

    @Operation(summary = "notifying attempt",
            description = "notifying by using clientId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "notification ends successfully", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TelegramSubscriber.class)
            )),
            @ApiResponse(responseCode = "403", description = "Unauthorized attempt", content = @Content(
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
        String message = telegramService.notifyClientStatus(clientId);
        return ResponseEntity.ok()
                .header("Content-Type", "text/plain")
                .body(message);
    }

    @Operation(summary = "registering client ",
            description = "registering client client by using firstName, lastName ,chatId, clientId ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "registration ends successfully", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TelegramSubscriber.class)
            )),
            @ApiResponse(responseCode = "403", description = "Unauthorized attempt", content = @Content(
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
        return telegramService.registerSubscriber(chatId, firstName, lastName, clientId);


    }
}
