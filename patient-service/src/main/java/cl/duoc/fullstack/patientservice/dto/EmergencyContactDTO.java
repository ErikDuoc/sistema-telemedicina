package cl.duoc.fullstack.patientservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record EmergencyContactDTO(
    @Schema(description = "Identificador único del contacto de emergencia", example = "1")
    Long id,

    @Schema(description = "Nombre completo del contacto de emergencia", example = "María González López", requiredMode = Schema.RequiredMode.REQUIRED)
    String nombre,

    @Schema(description = "Relación del contacto con el paciente", example = "Madre", requiredMode = Schema.RequiredMode.REQUIRED)
    String parentesco,

    @Schema(description = "Número de teléfono del contacto de emergencia", example = "+56912345678", requiredMode = Schema.RequiredMode.REQUIRED)
    String telefono
) {}

