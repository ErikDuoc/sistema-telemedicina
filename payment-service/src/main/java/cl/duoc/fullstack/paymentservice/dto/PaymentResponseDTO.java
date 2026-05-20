package cl.duoc.fullstack.paymentservice.dto;

import cl.duoc.fullstack.paymentservice.model.PaymentMethod;
import cl.duoc.fullstack.paymentservice.model.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponseDTO {

    private Long id;

    private Long appointmentId;

    private Double amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus status;

    private LocalDateTime createdAt;
}
