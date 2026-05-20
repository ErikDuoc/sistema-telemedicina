package cl.duoc.fullstack.clinicalrecordservice.exception;

public class DuplicateClinicalRecordException extends RuntimeException {

    public DuplicateClinicalRecordException(String message){
        super(message);
    }
}