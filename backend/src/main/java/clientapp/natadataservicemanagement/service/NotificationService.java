package clientapp.natadataservicemanagement.service;

import clientapp.natadataservicemanagement.model.Client;
import clientapp.natadataservicemanagement.model.EmailTemplate;
import clientapp.natadataservicemanagement.model.NotificationType;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final EmailNotificationService emailService;
    private final EmailTemplateService emailTemplateService;

    public void sendNotification(NotificationType notification, Client client) {

        if (!client.isNotifyEmail()) {
            return;
        }

        EmailTemplate tpl = emailTemplateService.build(notification, client);

        emailService.sendEmail(
                client.getEmail(),
                tpl.getName(),
                tpl.getDescription()
        );

        logger.info("Email sent to {}", client.getEmail());
    }
}