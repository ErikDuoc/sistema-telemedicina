package cl.duoc.fullstack.patientservice.exception;

public class DuplicatePatientException extends RuntimeException {

    public DuplicatePatientException(String message) {
        super(message);
    }
}
