package cl.duoc.fullstack.videoconsultationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VideoConsultationRequestDTO {

    @Schema(
            description = "ID de la cita médica asociada",
            example = "9",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private Long appointmentId;

    @Schema(
            description = "Plataforma utilizada para la videollamada",
            example = "ZOOM",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private String platform;
}