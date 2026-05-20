package cl.duoc.fullstack.patientservice.exception;

/**
 * Excepción lanzada cuando se intenta crear un recurso que ya existe.
 * Por ejemplo: crear un paciente con RUT duplicado o email duplicado.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}

