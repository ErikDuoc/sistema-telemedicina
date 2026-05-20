package cl.duoc.fullstack.patientservice.exception;

/**
 * Excepción lanzada cuando hay un error de validación o negocio.
 * HTTP Status: 400 BAD_REQUEST
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

