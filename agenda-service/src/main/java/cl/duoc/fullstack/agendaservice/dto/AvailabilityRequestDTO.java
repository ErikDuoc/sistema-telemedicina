package cl.duoc.fullstack.agendaservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailabilityRequestDTO {

    @NotNull
    private Long doctorId;

    @NotBlank
    private String dayOfWeek;

    @NotBlank
    private String startTime;

    @NotBlank
    private String endTime;
}
