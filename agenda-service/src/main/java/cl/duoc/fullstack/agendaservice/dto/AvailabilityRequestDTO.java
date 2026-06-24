package cl.duoc.fullstack.agendaservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailabilityRequestDTO {

    @Schema(description = "ID del médico", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long doctorId;

    @Schema(description = "Día de la semana (MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY)", example = "MONDAY", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String dayOfWeek;

    @Schema(description = "Hora de inicio de disponibilidad (formato HH:mm)", example = "09:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String startTime;

    @Schema(description = "Hora de fin de disponibilidad (formato HH:mm)", example = "17:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String endTime;
}
