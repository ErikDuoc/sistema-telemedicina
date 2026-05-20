package cl.duoc.fullstack.videoconsultationservice.exception;

public class DuplicateVideoConsultationException extends RuntimeException {

    public DuplicateVideoConsultationException(String message){
        super(message);
    }
}