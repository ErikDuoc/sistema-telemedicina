package cl.duoc.fullstack.doctorservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpecialtyDTO {

    private Long id;

    private String name;

    private String description;
}
