package cl.duoc.fullstack.laboratoryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabOrderRequestDTO {

    @NotNull
    private Long patientId;

    @NotNull
    private Long doctorId;

    @NotBlank
    private String examType;
}
