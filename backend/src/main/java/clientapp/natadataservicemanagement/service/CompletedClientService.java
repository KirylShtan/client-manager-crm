package clientapp.natadataservicemanagement.service;

import clientapp.natadataservicemanagement.exception.ClientNotFoundException;
import clientapp.natadataservicemanagement.model.CompletedClient;
import clientapp.natadataservicemanagement.repository.CompletedClientRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompletedClientService extends BasicClientServiceImpl<CompletedClient> {

    private static final Logger logger = LoggerFactory.getLogger(CompletedClientService.class);
    @Autowired
    public CompletedClientService(CompletedClientRepository completedClientRepository) {
        super(completedClientRepository);

    }



}
