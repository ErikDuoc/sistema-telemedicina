package cl.duoc.fullstack.clinicalrecordservice.controller;

import cl.duoc.fullstack.clinicalrecordservice.dto.*;
import cl.duoc.fullstack.clinicalrecordservice.service.ClinicalRecordService;
import cl.duoc.fullstack.clinicalrecordservice.service.ClinicalRecordLinkAssembler;
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

@Tag(name = "Fichas Clínicas", description = "Operaciones para gestionar fichas clínicas de pacientes")
@RestController
@RequestMapping("/api/clinical-records")
@RequiredArgsConstructor
public class ClinicalRecordController {

    private final ClinicalRecordService service;
    private final ClinicalRecordLinkAssembler clinicalRecordLinkAssembler;

    @Operation(summary = "Crear ficha clínica", description = "Crea una nueva ficha clínica asociada a una cita médica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ficha clínica creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClinicalRecordResponseDTO create(@Valid @RequestBody ClinicalRecordRequestDTO request){
        return service.create(request);
    }

    @Operation(summary = "Obtener ficha clínica por ID", description = "Obtiene una ficha clínica específica por su identificador con enlaces HATEOAS en _links")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ficha clínica encontrada"),
            @ApiResponse(responseCode = "404", description = "Ficha clínica no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ClinicalRecordResponseDTO>> getById(@PathVariable Long id){
        return ResponseEntity.ok(clinicalRecordLinkAssembler.toModel(service.getById(id)));
    }
}