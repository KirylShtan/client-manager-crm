package clientapp.natadataservicemanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "telegram_subscriber")
public class TelegramSubscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    private ActualClient client;

    @Column(nullable = false, unique = true)
    private Long chatId;

    private String firstName;
    private String lastName;
}