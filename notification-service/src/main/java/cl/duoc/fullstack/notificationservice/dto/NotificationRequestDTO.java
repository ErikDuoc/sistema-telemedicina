package cl.duoc.fullstack.notificationservice.dto;

import cl.duoc.fullstack.notificationservice.model.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequestDTO {

    @Schema(description = "ID del destinatario de la notificación", example = "7", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long recipientId;

    @Schema(description = "Tipo de notificación (EMAIL, SMS, PUSH)", example = "EMAIL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private NotificationType type;

    @Schema(description = "Email o teléfono del destinatario", example = "paciente@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String recipient;

    @Schema(description = "Contenido del mensaje a enviar", example = "Su cita fue confirmada para el 15 de julio", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String message;
}
