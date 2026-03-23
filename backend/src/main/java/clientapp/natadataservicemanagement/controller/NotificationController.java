package clientapp.natadataservicemanagement.controller;


import clientapp.natadataservicemanagement.model.NotificationType;
import clientapp.natadataservicemanagement.repository.ActualClientRepository;
import clientapp.natadataservicemanagement.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final ActualClientRepository actualClientRepository;



    @Operation(
            summary = "Sending notification by email",
            description = "Sending notification by using clientId"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "notification sent successfully",content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotificationType.class)
            )),
            @ApiResponse(responseCode = "404", description = "Didn't found any client with such id",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotificationType.class)
            )),
            @ApiResponse(responseCode = "500",description = "Unexpected behaviour", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotificationType.class)
            ))
    })
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(
            @RequestParam Long clientId,
            @RequestParam NotificationType type
    ) {
        return actualClientRepository.findById(clientId)
                .map(client -> {
                    notificationService.sendNotification(type, client);
                    return ResponseEntity.ok("Notification sent to " + client.getEmail());
                })
                .orElse(ResponseEntity.badRequest().body("Client not found"));
    }

}
