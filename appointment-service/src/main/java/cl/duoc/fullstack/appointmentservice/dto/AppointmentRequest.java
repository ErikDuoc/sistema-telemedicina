package cl.duoc.fullstack.appointmentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentRequest {

    @Schema(description = "ID del paciente para la cita", example = "4", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long patientId;

    @Schema(description = "ID del médico asignado", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long doctorId;

    @Schema(description = "Fecha de la cita (formato YYYY-MM-DD)", example = "2024-07-15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String date;

    @Schema(description = "Hora de la cita (formato HH:mm)", example = "14:30", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String time;
}