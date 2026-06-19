package cl.duoc.fullstack.doctorservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    // Se documenta DTO de error
    @Schema(example = "Doctor no encontrado")
    private String message;
}
