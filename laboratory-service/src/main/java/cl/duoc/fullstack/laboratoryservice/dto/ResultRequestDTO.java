package cl.duoc.fullstack.laboratoryservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultRequestDTO {

    @NotBlank
    private String findings;

    @NotBlank
    private String documentUrl;
}
