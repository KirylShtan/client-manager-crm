import clientapp.natadataservicemanagement.NataDataServiceManagementApplication;
import clientapp.natadataservicemanagement.service.EmailNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"SECURITY_USER_PASSWORD=admin123"})
@TestPropertySource(properties = {"SECURITY_USER_NAME=admin"})
@TestPropertySource(properties = {"VAULT_SECRET_TOKEN=dasdfshgerg2354257y3=ffe"})
@TestPropertySource(properties = {"JWT_SECRET_KEY=daagearthwrthgerferwh5342523fw"})
@TestPropertySource(properties = {"TELEGRAM_BOT_TOKEN=563452354hrtgery4uj5yh44g4"})
@TestPropertySource(properties = {"TELEGRAM_WEBHOOK_SECRET=adwgerwtyh45tr234r3hyb4h35"})
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
