package cl.duoc.fullstack.agendaservice.exception;

public class AgendaNotFoundException extends RuntimeException {

    public AgendaNotFoundException(String message) {
        super(message);
    }
}
