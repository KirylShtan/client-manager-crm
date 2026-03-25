package clientapp.natadataservicemanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"SECURITY_USER_PASSWORD=admin123"})
@TestPropertySource(properties = {"SECURITY_USER_NAME=admin"})
@TestPropertySource(properties = {"VAULT_SECRET_TOKEN=dummy-token"})
@TestPropertySource(properties = {"TELEGRAM_BOT_TOKEN=dummy-token"})
@TestPropertySource(properties = {"TELEGRAM_WEBHOOK_SECRET=dummy-token"})
@TestPropertySource(properties = {"JWT_SECRET_KEY=dummy-token"})
@SpringBootTest
class NataDataServiceManagementApplicationTests {

    @Test
    void contextLoads() {
    }

}
