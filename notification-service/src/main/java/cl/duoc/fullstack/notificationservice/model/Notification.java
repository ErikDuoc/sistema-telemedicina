package cl.duoc.fullstack.notificationservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recipientId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String recipient;

    @Column(length = 1000)
    private String message;

    private LocalDateTime sentAt;
}
