package clientapp.natadataservicemanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Setter
@Getter
@Entity
@Table(name = "telegram_subscriber")
public class TelegramSubscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // optional=false если клиент обязателен
    @JoinColumn(name = "client_id", nullable = false, foreignKey = @ForeignKey(name = "FK_telegram_actual"))
    private ActualClient client;

    private Long chatId;
}
