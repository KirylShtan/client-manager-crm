package clientapp.natadataservicemanagement.repository;

import clientapp.natadataservicemanagement.model.TelegramSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TelegramSubscriberRepository extends JpaRepository<TelegramSubscriber,Long> {
    Optional<TelegramSubscriber> findByClient_Id(Long clientId);
}
