package clientapp.natadataservicemanagement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
@EnableScheduling
@SpringBootApplication
public class NataDataServiceManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(NataDataServiceManagementApplication.class, args);
    }
    @Value("${VAULT_SECRET_TOKEN}")
    private String VAULT_SECRET_TOKEN;
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();

    }
    @Bean
    public VaultTemplate vaultTemplate() {
        VaultEndpoint endpoint = VaultEndpoint.create("localhost", 8200);
        endpoint.setScheme("http");
        ClientAuthentication clientAuth = new TokenAuthentication(VAULT_SECRET_TOKEN);

        return new VaultTemplate(endpoint, clientAuth);
    }
    public void saveSecret(VaultTemplate vaultTemplate, String key, Map<String, Object> data) {
        VaultKeyValueOperations kvOps = vaultTemplate.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);
        kvOps.put(key, data);
    }


}
