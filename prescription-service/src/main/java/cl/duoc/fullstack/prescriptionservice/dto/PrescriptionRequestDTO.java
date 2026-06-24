package cl.duoc.fullstack.prescriptionservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrescriptionRequestDTO {

    @Schema(description = "ID de la ficha clínica asociada", example = "8", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long clinicalRecordId;

    @Schema(description = "ID del paciente", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long patientId;

    @Schema(description = "Nombre del medicamento prescrito", example = "Ibuprofeno 400mg", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String medication;

    @Schema(description = "Indicaciones de uso del medicamento", example = "1 comprimido cada 8 horas después de las comidas", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String indications;
}