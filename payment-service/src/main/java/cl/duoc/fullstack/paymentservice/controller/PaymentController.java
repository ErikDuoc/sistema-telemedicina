package cl.duoc.fullstack.paymentservice.controller;

import cl.duoc.fullstack.paymentservice.dto.InsuranceDTO;
import cl.duoc.fullstack.paymentservice.dto.PaymentRequestDTO;
import cl.duoc.fullstack.paymentservice.dto.PaymentResponseDTO;
import cl.duoc.fullstack.paymentservice.service.PaymentService;
import cl.duoc.fullstack.paymentservice.service.InsuranceLinkAssembler;
import cl.duoc.fullstack.paymentservice.service.PaymentLinkAssembler;
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
    private final PaymentLinkAssembler paymentLinkAssembler;

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

    @Operation(summary = "Obtener pago por ID", description = "Obtiene los detalles de una transacción de pago específica con enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PaymentResponseDTO>> getById(@PathVariable Long id) {
        PaymentResponseDTO payment = paymentService.getById(id);
        EntityModel<PaymentResponseDTO> model = paymentLinkAssembler.toModel(payment);
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Obtener todos los pagos", description = "Obtiene todas las transacciones de pago registradas con enlaces HATEOAS")
    @ApiResponse(responseCode = "200", description = "Pagos obtenidos exitosamente")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PaymentResponseDTO>>> getAll() {
        List<EntityModel<PaymentResponseDTO>> payments = paymentService.getAll().stream()
                .map(paymentLinkAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<PaymentResponseDTO>> collection = CollectionModel.of(payments);
        collection.add(linkTo(methodOn(PaymentController.class).getAll()).withSelfRel());

        return ResponseEntity.ok(collection);
    }
}
