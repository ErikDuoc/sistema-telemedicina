package cl.duoc.fullstack.videoconsultationservice.controller;

import cl.duoc.fullstack.videoconsultationservice.dto.*;
import cl.duoc.fullstack.videoconsultationservice.service.VideoConsultationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Consultas Videollamada", description = "Operaciones para gestionar consultas médicas por videollamada")
@RestController
@RequestMapping("/api/video-consultations")
@RequiredArgsConstructor
public class VideoConsultationController {

    private final VideoConsultationService service;

    @Operation(summary = "Crear consulta por videollamada", description = "Crea una nueva consulta médica por videollamada asociada a una cita")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Consulta por videollamada creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public VideoConsultationResponseDTO create(@Valid @RequestBody VideoConsultationRequestDTO request) {
        return service.create(request);
    }

    @Operation(summary = "Obtener consulta por videollamada por ID", description = "Obtiene una consulta médica por videollamada específica según su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta por videollamada encontrada"),
            @ApiResponse(responseCode = "404", description = "Consulta por videollamada no encontrada")
    })
    @GetMapping("/{id}")
    public VideoConsultationResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }
}