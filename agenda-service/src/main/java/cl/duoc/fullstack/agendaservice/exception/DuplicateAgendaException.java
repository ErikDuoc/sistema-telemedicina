package cl.duoc.fullstack.agendaservice.exception;

public class DuplicateAgendaException extends RuntimeException {

    public DuplicateAgendaException(String message) {
        super(message);
    }
}
