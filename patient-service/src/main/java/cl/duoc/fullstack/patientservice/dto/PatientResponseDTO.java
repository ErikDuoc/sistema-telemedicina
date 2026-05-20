package cl.duoc.fullstack.patientservice.dto;

import java.time.LocalDate;
import java.util.List;

public record PatientResponseDTO(
    Long id,
    String rut,
    String nombre,
    String apellido,
    LocalDate fechaNacimiento,
    String genero,
    String email,
    String prevision,
    List<EmergencyContactDTO> contactosEmergencia
) {}

