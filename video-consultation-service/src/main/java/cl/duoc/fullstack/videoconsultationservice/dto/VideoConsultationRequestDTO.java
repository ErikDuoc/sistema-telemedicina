package cl.duoc.fullstack.videoconsultationservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VideoConsultationRequestDTO {

    @NotNull
    private Long appointmentId;

    @NotNull
    private String platform;
}