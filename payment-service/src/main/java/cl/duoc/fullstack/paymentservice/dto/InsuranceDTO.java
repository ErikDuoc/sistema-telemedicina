package cl.duoc.fullstack.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InsuranceDTO {

    @Schema(description = "ID único del seguro", example = "1")
    private Long id;

    @Schema(description = "Nombre de la aseguradora", example = "Fonasa")
    private String name;

    @Schema(description = "Porcentaje de cobertura del seguro", example = "80")
    private Double coveragePercentage;
}
