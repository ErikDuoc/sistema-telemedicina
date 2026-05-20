package cl.duoc.fullstack.clinicalrecordservice.exception;

public class ClinicalRecordNotFoundException extends RuntimeException {

    public ClinicalRecordNotFoundException(String message){
        super(message);
    }
}