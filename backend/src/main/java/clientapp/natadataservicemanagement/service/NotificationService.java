package clientapp.natadataservicemanagement.service;

import clientapp.natadataservicemanagement.model.Client;
import clientapp.natadataservicemanagement.model.EmailTemplate;
import clientapp.natadataservicemanagement.model.NotificationType;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;
import clientapp.natadataservicemanagement.dto.SendResult;

@AllArgsConstructor
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final EmailNotificationService emailService;
    private final EmailTemplateService emailTemplateService;

    public CompletableFuture<SendResult> sendNotification(NotificationType notification, Client client) {
        EmailTemplate tpl = emailTemplateService.build(notification, client);

        return emailService.sendEmail(
                client.getEmail(),
                tpl.getName(),
                tpl.getDescription()
        ).whenComplete((result, ex) -> {
            if (ex != null) {
                logger.error("Email async task failed for {}", client.getEmail(), ex);
            } else {
                logger.info("Email task finished for {}: success={}", client.getEmail(), result.success());
            }
        });
    }
}