package cl.duoc.fullstack.paymentservice.controller;

import cl.duoc.fullstack.paymentservice.dto.InsuranceDTO;
import cl.duoc.fullstack.paymentservice.dto.PaymentRequestDTO;
import cl.duoc.fullstack.paymentservice.dto.PaymentResponseDTO;
import cl.duoc.fullstack.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDTO processPayment(
            @Valid @RequestBody PaymentRequestDTO dto
    ) {

        return paymentService.processPayment(dto);
    }

    @GetMapping("/insurances")
    public List<InsuranceDTO> getAllInsurances() {

        return paymentService.getAllInsurances();
    }
}
