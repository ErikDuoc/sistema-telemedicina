package cl.duoc.fullstack.laboratoryservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabOrderRequestDTO {

    @Schema(description = "ID del paciente", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long patientId;

    @Schema(description = "ID del médico solicitante", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long doctorId;

    @Schema(description = "Tipo de examen a realizar", example = "Hemograma", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String examType;
}
