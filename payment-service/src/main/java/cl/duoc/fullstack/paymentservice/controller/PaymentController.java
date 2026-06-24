package cl.duoc.fullstack.paymentservice.controller;

import cl.duoc.fullstack.paymentservice.dto.InsuranceDTO;
import cl.duoc.fullstack.paymentservice.dto.PaymentRequestDTO;
import cl.duoc.fullstack.paymentservice.dto.PaymentResponseDTO;
import cl.duoc.fullstack.paymentservice.service.PaymentService;
import cl.duoc.fullstack.paymentservice.service.InsuranceLinkAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Pagos", description = "Operaciones para procesar pagos y gestionar seguros")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final InsuranceLinkAssembler insuranceLinkAssembler;

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

    @Operation(summary = "Obtener seguros disponibles", description = "Retorna la lista de todas las aseguradoras disponibles en el sistema con enlaces HATEOAS en _links")
    @ApiResponse(responseCode = "200", description = "Seguros obtenidos exitosamente")
    @GetMapping("/insurances")
    public ResponseEntity<CollectionModel<EntityModel<InsuranceDTO>>> getAllInsurances() {
        List<EntityModel<InsuranceDTO>> insurances = paymentService.getAllInsurances().stream()
                .map(insuranceLinkAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<InsuranceDTO>> collection = CollectionModel.of(insurances);
        collection.add(linkTo(methodOn(PaymentController.class).getAllInsurances()).withSelfRel());

        return ResponseEntity.ok(collection);
    }
}
