package cl.duoc.fullstack.patientservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * Respuesta estandarizada para todos los errores de la API.
 * Mantiene coherencia en la presentación de excepciones.
 */
@Builder
public record ApiErrorResponse(
        int status,
        String error,
        String message,
        String path,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,
        String correlationId
) {
    /**
     * Constructor simplificado para backward compatibility
     */
    public static ApiErrorResponse of(int status, String error, String message) {
        return ApiErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

