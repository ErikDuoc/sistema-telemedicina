package cl.duoc.fullstack.patientservice.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record PatientRequestDTO(
    @NotBlank(message = "El RUT es obligatorio")
    String rut,
    @NotBlank(message = "El nombre es obligatorio")
    String nombre,
    @NotBlank(message = "El apellido es obligatorio")
    String apellido,
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    LocalDate fechaNacimiento,
    @NotBlank(message = "El género es obligatorio")
    String genero,
    @Email(message = "Email inválido")
    @NotBlank(message = "El email es obligatorio")
    String email,
    @NotBlank(message = "La previsión es obligatoria")
    String prevision,
    @NotNull(message = "Debe incluir al menos un contacto de emergencia")
    List<EmergencyContactDTO> contactosEmergencia
) {}

