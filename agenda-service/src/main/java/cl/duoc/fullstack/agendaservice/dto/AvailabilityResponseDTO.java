package cl.duoc.fullstack.agendaservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AvailabilityResponseDTO {

    private Long id;
    private Long doctorId;
    private String doctorName;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
}
