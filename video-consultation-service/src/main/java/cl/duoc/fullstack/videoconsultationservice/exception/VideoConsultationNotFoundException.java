package cl.duoc.fullstack.videoconsultationservice.exception;

public class VideoConsultationNotFoundException extends RuntimeException {

    public VideoConsultationNotFoundException(String message){
        super(message);
    }
}