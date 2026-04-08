package clientapp.natadataservicemanagement.service;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class EmailNotificationTest {
    @Mock
    private JavaMailSender mailSender;
    @InjectMocks
    private EmailNotificationService emailNotificationService;
    @Test
    void sendEmail_shouldBuildAndSendMimeMessage() {
        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        emailNotificationService.sendEmail(
                "test@example.com",
                "Test letter",
                "Test email body"
        );
        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        MimeMessage sentMessage = messageCaptor.getValue();
        try {
            assertEquals("Test letter", sentMessage.getSubject());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}