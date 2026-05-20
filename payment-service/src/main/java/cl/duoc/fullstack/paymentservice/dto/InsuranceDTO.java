package cl.duoc.fullstack.paymentservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InsuranceDTO {

    private Long id;

    private String name;

    private Double coveragePercentage;
}
