package cl.duoc.fullstack.appointmentservice.exception;

public class DuplicateAppointmentException extends RuntimeException {

    public DuplicateAppointmentException(String message){
        super(message);
    }
}