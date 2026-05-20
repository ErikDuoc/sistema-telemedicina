package cl.duoc.fullstack.clinicalrecordservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClinicalRecordRequestDTO {

    @NotNull
    private Long appointmentId;

    @NotNull
    private Long patientId;

    @NotNull
    private Long doctorId;

    @NotNull
    private String diagnosis;

    @NotNull
    private String treatment;
}