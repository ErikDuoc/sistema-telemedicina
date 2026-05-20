package cl.duoc.fullstack.doctorservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DoctorResponseDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String nationalRegistry;

    private String email;

    private String specialtyName;
}
