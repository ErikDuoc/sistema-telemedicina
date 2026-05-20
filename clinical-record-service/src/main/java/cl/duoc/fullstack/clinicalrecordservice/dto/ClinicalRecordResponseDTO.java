package cl.duoc.fullstack.clinicalrecordservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalRecordResponseDTO {

    private Long id;
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private String diagnosis;
    private String treatment;
    private String createdAt;
}