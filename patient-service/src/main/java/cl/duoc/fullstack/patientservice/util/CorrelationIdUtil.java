package cl.duoc.fullstack.patientservice.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * Utilidad para manejar el Correlation ID en todo el sistema.
 * El Correlation ID es un identificador único para rastrear requests
 * a través de toda la cadena de microservicios.
 */
public class CorrelationIdUtil {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    /**
     * Obtiene el Correlation ID del request actual.
     * Si no existe, genera uno nuevo.
     *
     * @return correlation ID como String
     */
    public static String getCorrelationId() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String correlationId = request.getHeader(CORRELATION_ID_HEADER);
                if (correlationId != null && !correlationId.isEmpty()) {
                    return correlationId;
                }
            }
        } catch (Exception e) {
            // Si algo falla, generar uno nuevo
        }
        // Si no existe, generar uno nuevo
        return generateCorrelationId();
    }

    /**
     * Genera un nuevo Correlation ID usando UUID.
     *
     * @return UUID como String
     */
    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Establece un Correlation ID en el response header.
     *
     * @param correlationId ID a establecer
     */
    public static void setCorrelationIdInResponse(String correlationId) {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                attrs.getResponse().setHeader(CORRELATION_ID_HEADER, correlationId);
            }
        } catch (Exception e) {
            // Si falla, simplemente no hacemos nada
        }
    }
}

