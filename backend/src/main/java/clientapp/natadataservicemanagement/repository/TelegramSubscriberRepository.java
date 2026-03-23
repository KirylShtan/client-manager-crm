package clientapp.natadataservicemanagement.repository;

import clientapp.natadataservicemanagement.model.TelegramSubscriber;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TelegramSubscriberRepository extends JpaRepository<TelegramSubscriber, Long> {
    Optional<TelegramSubscriber> findByClient_Id(Long clientId);
    boolean existsByChatId(Long chatId);
    Optional<TelegramSubscriber> findByChatId(Long chatId);
    @Modifying
    @Transactional
    @Query("DELETE FROM TelegramSubscriber t WHERE t.client.id = :clientId")
    void deleteByClientId(@Param("clientId") Long clientId);


}