package cl.duoc.fullstack.patientservice.dto;

public record EmergencyContactDTO(
    Long id,
    String nombre,
    String parentesco,
    String telefono
) {}

