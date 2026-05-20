package cl.duoc.fullstack.paymentservice.dto;

import cl.duoc.fullstack.paymentservice.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDTO {

    @NotNull
    private Long appointmentId;

    @NotNull
    @Positive
    private Double amount;

    @NotNull
    private PaymentMethod paymentMethod;
}
