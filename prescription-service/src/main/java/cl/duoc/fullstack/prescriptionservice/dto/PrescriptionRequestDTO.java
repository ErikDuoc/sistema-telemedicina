package cl.duoc.fullstack.prescriptionservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrescriptionRequestDTO {

    @NotNull
    private Long clinicalRecordId;

    @NotNull
    private Long patientId;

    @NotNull
    private String medication;

    @NotNull
    private String indications;
}