package clientapp.natadataservicemanagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.VaultException;
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
        try{
        VaultKeyValueOperations kvOps = vaultTemplate.opsForKeyValue(
                "secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2
        );
        var response = kvOps.get(vaultKey);
        if (response == null || response.getRequiredData() == null){
        logger.warn("No data found for vault key {}", vaultKey);
        return null;

        }
        return (String) response.getRequiredData().get("password");
        }catch(VaultException e){
            logger.error("Error getting data for vault key {}", vaultKey, e);
        }catch(NullPointerException e){
            logger.error("Unexpected null encountered while getting password for key: {}", vaultKey, e);

        }
        return null;

    }




}