package cl.duoc.fullstack.laboratoryservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultRequestDTO {

    @Schema(description = "Hallazgos del examen", example = "Hemoglobina baja, VCM normal", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String findings;

    @Schema(description = "URL del documento con los resultados", example = "https://lab-system.com/risultati/12345.pdf", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String documentUrl;
}
