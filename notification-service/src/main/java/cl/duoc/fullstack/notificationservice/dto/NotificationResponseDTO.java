package cl.duoc.fullstack.notificationservice.dto;

import cl.duoc.fullstack.notificationservice.model.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponseDTO {

    private Long id;

    private Long recipientId;

    private NotificationType type;

    private String recipient;

    private String message;

    private LocalDateTime sentAt;
}
