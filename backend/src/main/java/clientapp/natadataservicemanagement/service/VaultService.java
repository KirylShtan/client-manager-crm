package clientapp.natadataservicemanagement.service;

import clientapp.natadataservicemanagement.exception.VaultAccessException;
import clientapp.natadataservicemanagement.exception.VaultDataNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.server.ResponseStatusException;


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

        try {
            VaultKeyValueOperations kvOps = vaultTemplate.opsForKeyValue(
                    "secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2
            );

            var response = kvOps.get(vaultKey);

            if (response == null) {
                throw new VaultDataNotFoundException("No data for key: " + vaultKey);
            }
            var data = response.getData();

            if (data == null || !data.containsKey("password")) {
                throw new VaultDataNotFoundException("Password not found");

            }

            return (String) data.get("password");

        } catch (VaultException e) {
            logger.error("Vault error for key {}", vaultKey, e);
            throw new VaultAccessException("Error accessing Vault for key: " + vaultKey);
        }
    }




}