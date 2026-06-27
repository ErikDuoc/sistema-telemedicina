package cl.duoc.fullstack.videoconsultationservice.controller;

import cl.duoc.fullstack.videoconsultationservice.dto.*;
import cl.duoc.fullstack.videoconsultationservice.service.VideoConsultationService;
import cl.duoc.fullstack.videoconsultationservice.service.VideoConsultationLinkAssembler;
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

@Tag(name = "Consultas Videollamada", description = "Operaciones para gestionar consultas médicas por videollamada")
@RestController
@RequestMapping("/api/video-consultations")
@RequiredArgsConstructor
public class VideoConsultationController {

    private final VideoConsultationService service;
    private final VideoConsultationLinkAssembler videoConsultationLinkAssembler;

    @Operation(summary = "Crear consulta por videollamada", description = "Crea una nueva consulta médica por videollamada asociada a una cita")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Consulta por videollamada creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VideoConsultationResponseDTO create(@Valid @RequestBody VideoConsultationRequestDTO request) {
        return service.create(request);
    }

    @Operation(summary = "Obtener consulta por videollamada por ID", description = "Obtiene una consulta médica por videollamada específica según su identificador con enlaces HATEOAS en _links")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta por videollamada encontrada"),
            @ApiResponse(responseCode = "404", description = "Consulta por videollamada no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<VideoConsultationResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(videoConsultationLinkAssembler.toModel(service.getById(id)));
    }
}