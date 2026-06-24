package cl.duoc.fullstack.patientservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record PatientRequestDTO(
    @Schema(description = "RUT del paciente en formato chileno", example = "20.123.456-7", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El RUT es obligatorio")
    String rut,

    @Schema(description = "Nombre o nombres del paciente", example = "Carlos", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    @Schema(description = "Apellido paterno y/o materno del paciente", example = "González Martínez", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El apellido es obligatorio")
    String apellido,

    @Schema(description = "Fecha de nacimiento del paciente", example = "1990-05-15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    LocalDate fechaNacimiento,

    @Schema(description = "Género del paciente", example = "Masculino", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El género es obligatorio")
    String genero,

    @Schema(description = "Correo electrónico del paciente", example = "carlos.gonzalez@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "Email inválido")
    @NotBlank(message = "El email es obligatorio")
    String email,

    @Schema(description = "Previsión de salud (FONASA, ISAPRE, etc.)", example = "FONASA", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La previsión es obligatoria")
    String prevision,

    @Schema(description = "Lista de contactos de emergencia del paciente", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Debe incluir al menos un contacto de emergencia")
    List<EmergencyContactDTO> contactosEmergencia
) {}

