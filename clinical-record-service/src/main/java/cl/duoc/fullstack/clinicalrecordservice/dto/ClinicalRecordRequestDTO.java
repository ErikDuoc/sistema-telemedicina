package cl.duoc.fullstack.clinicalrecordservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClinicalRecordRequestDTO {

    @Schema(description = "ID de la cita médica", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long appointmentId;

    @Schema(description = "ID del paciente", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long patientId;

    @Schema(description = "ID del médico", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long doctorId;

    @Schema(description = "Diagnóstico médico", example = "Hipertensión arterial", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String diagnosis;

    @Schema(description = "Tratamiento recomendado", example = "Medicación antihipertensiva", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String treatment;
}