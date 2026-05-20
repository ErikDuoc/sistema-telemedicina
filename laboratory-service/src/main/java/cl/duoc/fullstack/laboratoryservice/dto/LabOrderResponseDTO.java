package cl.duoc.fullstack.laboratoryservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LabOrderResponseDTO {

    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String examType;
    private String status;
}
