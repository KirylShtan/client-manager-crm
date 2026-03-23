package clientapp.natadataservicemanagement.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class EmailNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);
    private final JavaMailSender mailSender;
    @Autowired
    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    @Async
    public void sendEmail(String to, String subject, String text) {
        logger.info("Preparing to send email to {}", to);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");

            helper.setFrom("gambrinas13kn@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text,true);

            mailSender.send(message);
            logger.info("Email actually sent to {}", to);
        } catch (Exception e){
            logger.error("Error sending email", e);
        }
    }
}
