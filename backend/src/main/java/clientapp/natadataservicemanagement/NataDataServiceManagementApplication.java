package clientapp.natadataservicemanagement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.vault.authentication.SimpleSessionManager;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Map;
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class NataDataServiceManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(NataDataServiceManagementApplication.class, args);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

   
    @Bean
    public VaultTemplate vaultTemplate(
            @Value("${SPRING_CLOUD_VAULT_URI}") String vaultUri,
            @Value("${VAULT_SECRET_TOKEN}") String vaultToken,
            @Value("${spring.cloud.vault.ssl.trust-store-password:vaultdev}") String trustStorePassword
    ) throws Exception {
        URI uri = URI.create(vaultUri);
        VaultEndpoint endpoint = VaultEndpoint.create(uri.getHost(), uri.getPort());
        endpoint.setScheme(uri.getScheme());

        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (InputStream is = new ClassPathResource("vault-truststore.jks").getInputStream()) {
            trustStore.load(is, trustStorePassword.toCharArray());
        }
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());

        HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext).build();
        ClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);

        return new VaultTemplate(endpoint, requestFactory, new SimpleSessionManager(new TokenAuthentication(vaultToken)));
    }
    public void saveSecret(VaultTemplate vaultTemplate, String key, Map<String, Object> data) {
        VaultKeyValueOperations kvOps = vaultTemplate.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);
        kvOps.put(key, data);
    }


}
