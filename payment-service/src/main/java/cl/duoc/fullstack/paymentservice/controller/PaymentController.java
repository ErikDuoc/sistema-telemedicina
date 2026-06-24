package cl.duoc.fullstack.paymentservice.controller;

import cl.duoc.fullstack.paymentservice.dto.InsuranceDTO;
import cl.duoc.fullstack.paymentservice.dto.PaymentRequestDTO;
import cl.duoc.fullstack.paymentservice.dto.PaymentResponseDTO;
import cl.duoc.fullstack.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pagos", description = "Operaciones para procesar pagos y gestionar seguros")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Procesar pago", description = "Procesa un pago para una cita médica específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pago procesado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/process")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDTO processPayment(
            @Valid @RequestBody PaymentRequestDTO dto
    ) {

        return paymentService.processPayment(dto);
    }

    @Operation(summary = "Obtener seguros disponibles", description = "Retorna la lista de todas las aseguradoras disponibles en el sistema")
    @ApiResponse(responseCode = "200", description = "Seguros obtenidos exitosamente")
    @GetMapping("/insurances")
    public List<InsuranceDTO> getAllInsurances() {

        return paymentService.getAllInsurances();
    }
}
