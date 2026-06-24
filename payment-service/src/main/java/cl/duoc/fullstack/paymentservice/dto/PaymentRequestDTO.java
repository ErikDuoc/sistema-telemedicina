package cl.duoc.fullstack.paymentservice.dto;

import cl.duoc.fullstack.paymentservice.model.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDTO {

    @Schema(description = "ID de la cita a pagar", example = "6", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long appointmentId;

    @Schema(description = "Monto a pagar en pesos chilenos", example = "50000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Positive
    private Double amount;

    @Schema(description = "Método de pago (CASH, CARD, TRANSFER)", example = "CARD", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private PaymentMethod paymentMethod;
}
