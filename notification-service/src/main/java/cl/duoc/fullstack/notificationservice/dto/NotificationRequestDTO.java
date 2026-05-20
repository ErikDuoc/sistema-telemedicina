package cl.duoc.fullstack.notificationservice.dto;

import cl.duoc.fullstack.notificationservice.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequestDTO {

    @NotNull
    private Long recipientId;

    @NotNull
    private NotificationType type;

    @NotBlank
    private String recipient;

    @NotBlank
    private String message;
}
