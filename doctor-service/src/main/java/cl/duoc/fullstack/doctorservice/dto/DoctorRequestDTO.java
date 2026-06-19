package cl.duoc.fullstack.doctorservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorRequestDTO {

    // Se documenta schema para swagger
    @Schema(description = "Primer nombre del médico", example = "Juan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String firstName;

    @Schema(description = "Apellido paterno y/o materno del médico", example = "Pérez")
    @NotBlank
    private String lastName;

    @Schema(description = "Rut de profesional", example = "10.111.222-K")
    @NotBlank
    private String nationalRegistry;

    @Schema(description = "Correo electrónico institucional o de contacto del médico", example = "juan.perez@hospital.com")
    @Email
    private String email;

    private Long specialtyId;
}
