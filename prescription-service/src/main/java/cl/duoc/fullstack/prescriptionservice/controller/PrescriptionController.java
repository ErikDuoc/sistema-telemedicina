package cl.duoc.fullstack.prescriptionservice.controller;

import cl.duoc.fullstack.prescriptionservice.dto.*;
import cl.duoc.fullstack.prescriptionservice.service.PrescriptionService;
import cl.duoc.fullstack.prescriptionservice.service.PrescriptionLinkAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Recetas Médicas", description = "Operaciones para gestionar recetas médicas")
@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService service;
    private final PrescriptionLinkAssembler prescriptionLinkAssembler;

    @Operation(summary = "Crear receta médica", description = "Crea una nueva receta médica asociada a una ficha clínica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Receta creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrescriptionResponseDTO create(@Valid @RequestBody PrescriptionRequestDTO request){
        return service.create(request);
    }

    @Operation(summary = "Obtener receta por ID", description = "Obtiene una receta médica específica por su identificador con enlaces HATEOAS en _links")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receta encontrada"),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PrescriptionResponseDTO>> getById(@PathVariable Long id){
        return ResponseEntity.ok(prescriptionLinkAssembler.toModel(service.getById(id)));
    }
}