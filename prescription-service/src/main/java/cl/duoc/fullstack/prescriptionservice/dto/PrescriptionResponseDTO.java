package cl.duoc.fullstack.prescriptionservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponseDTO {

    private Long id;
    private Long clinicalRecordId;
    private Long patientId;
    private String medication;
    private String indications;
    private String createdAt;
}