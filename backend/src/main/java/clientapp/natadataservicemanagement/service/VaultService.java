package clientapp.natadataservicemanagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;



import java.util.Map;

@Service
public class VaultService {
    private final static Logger logger = LoggerFactory.getLogger(VaultService.class);

    private final VaultTemplate vaultTemplate;

    @Autowired
    public VaultService(VaultTemplate vaultTemplate) {
        this.vaultTemplate = vaultTemplate;
    }


    public void saveClientPassword(String vaultKey, String realPassword) {
        VaultKeyValueOperations kvOps = vaultTemplate.opsForKeyValue(
                "secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2
        );

        kvOps.put(vaultKey, Map.of("password", realPassword));
    }


    public String getClientPassword(String vaultKey) {
        VaultKeyValueOperations kvOps = vaultTemplate.opsForKeyValue(
                "secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2
        );
        Map<String, Object> data = kvOps.get(vaultKey).getRequiredData();
        return data != null ? (String) data.get("password") : null;
    }


}