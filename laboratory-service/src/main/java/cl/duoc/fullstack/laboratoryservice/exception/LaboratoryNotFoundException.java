package cl.duoc.fullstack.laboratoryservice.exception;

public class LaboratoryNotFoundException extends RuntimeException {

    public LaboratoryNotFoundException(String message) {
        super(message);
    }
}
