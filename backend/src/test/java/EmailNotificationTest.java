import clientapp.natadataservicemanagement.NataDataServiceManagementApplication;
import clientapp.natadataservicemanagement.service.EmailNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest(classes = NataDataServiceManagementApplication.class)
public class EmailNotificationTest {

    private EmailNotificationService emailNotificationService;

    @Autowired
    public void setEmailNotificationService(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }
    @Test
    public void emailNotificationTest() {
        emailNotificationService.sendEmail(
                "nataliaorlova2219@gmail.com",
                "Test letter",
                "Ну здарова, это тестовая рассылка писем через мое приложение которое я создал"
        );
    }
}
