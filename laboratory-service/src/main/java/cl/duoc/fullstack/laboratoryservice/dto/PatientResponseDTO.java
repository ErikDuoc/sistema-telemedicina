package cl.duoc.fullstack.laboratoryservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
