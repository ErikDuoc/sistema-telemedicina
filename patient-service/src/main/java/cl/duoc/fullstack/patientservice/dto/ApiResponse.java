package cl.duoc.fullstack.patientservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Respuesta estándar para todos los endpoints del sistema.
 * Proporciona consistencia en el formato de respuestas REST.
 *
 * Estructura:
 * - success: indica si la operación fue exitosa
 * - message: mensaje descriptivo (usuario-friendly)
 * - data: datos retornados (puede ser null)
 * - timestamp: marca de tiempo de la respuesta
 * - correlationId: ID único para trazabilidad de requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Indica si la operación fue exitosa.
     */
    private boolean success;

    /**
     * Mensaje descriptivo de la respuesta.
     */
    private String message;

    /**
     * Datos de respuesta (genérico T).
     */
    private T data;

    /**
     * Timestamp de la respuesta.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * ID de correlación para trazabilidad.
     */
    private String correlationId;

    /**
     * Constructor para respuesta exitosa.
     */
    public static <T> ApiResponse<T> success(T data, String message, String correlationId) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .correlationId(correlationId)
                .build();
    }

    /**
     * Constructor para respuesta fallida.
     */
    public static <T> ApiResponse<T> error(String message, String correlationId) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .correlationId(correlationId)
                .build();
    }

    /**
     * Constructor para respuesta fallida con datos adicionales.
     */
    public static <T> ApiResponse<T> error(T data, String message, String correlationId) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .correlationId(correlationId)
                .build();
    }
}

