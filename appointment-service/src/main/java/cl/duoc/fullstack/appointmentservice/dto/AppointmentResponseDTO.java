package cl.duoc.fullstack.appointmentservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {

    private Long id;
    private Long patientId;
    private Long doctorId;
    private String date;
    private String time;
    private String status;
}