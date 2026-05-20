package cl.duoc.fullstack.videoconsultationservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoConsultationResponseDTO {

    private Long id;
    private Long appointmentId;
    private String meetingUrl;
    private String platform;
    private String status;
    private String createdAt;
}