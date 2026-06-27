package cl.duoc.fullstack.laboratoryservice.controller;

import cl.duoc.fullstack.laboratoryservice.dto.*;
import cl.duoc.fullstack.laboratoryservice.service.LaboratoryService;
import cl.duoc.fullstack.laboratoryservice.service.LaboratoryLinkAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Laboratorio", description = "Operaciones para gestionar órdenes de laboratorio y resultados de exámenes")
@RestController
@RequestMapping("/api/lab")
@RequiredArgsConstructor
public class LaboratoryController {

    private final LaboratoryService laboratoryService;
    private final LaboratoryLinkAssembler laboratoryLinkAssembler;

    @Operation(summary = "Crear orden de laboratorio", description = "Crea una nueva orden de examen de laboratorio para un paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public LabOrderResponseDTO createOrder(
            @Valid @RequestBody LabOrderRequestDTO dto
    ) {
        return laboratoryService.createOrder(dto);
    }

    @Operation(summary = "Cargar resultado de examen", description = "Carga el resultado del examen para una orden existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado cargado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })

    @PutMapping("/results/{orderId}")
    public ResponseEntity<String> uploadResult(
            @PathVariable Long orderId,
            @Valid @RequestBody ResultRequestDTO dto
    ) {

        laboratoryService.uploadResult(orderId, dto);

        return ResponseEntity.ok("Result uploaded successfully");
    }

    @Operation(summary = "Obtener órdenes del paciente", description = "Obtiene todas las órdenes de laboratorio de un paciente específico con enlaces HATEOAS en _links")
    @ApiResponse(responseCode = "200", description = "Órdenes obtenidas exitosamente")
    @GetMapping("/patient/{id}")
    public ResponseEntity<CollectionModel<EntityModel<LabOrderResponseDTO>>> getPatientOrders(@PathVariable Long id) {
        List<EntityModel<LabOrderResponseDTO>> orders = laboratoryService.getPatientOrders(id).stream()
                .map(laboratoryLinkAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<LabOrderResponseDTO>> collection = CollectionModel.of(orders);
        collection.add(linkTo(methodOn(LaboratoryController.class).getPatientOrders(id)).withSelfRel());

        return ResponseEntity.ok(collection);
    }
}
