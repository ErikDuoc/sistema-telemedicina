package cl.duoc.fullstack.agendaservice.controller;

import cl.duoc.fullstack.agendaservice.dto.AvailabilityRequestDTO;
import cl.duoc.fullstack.agendaservice.dto.AvailabilityResponseDTO;
import cl.duoc.fullstack.agendaservice.service.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Agenda y Disponibilidad", description = "Operaciones para gestionar agendas y disponibilidad de médicos")
@RestController
@RequestMapping("/api/agenda")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @Operation(summary = "Configurar disponibilidad", description = "Establece los horarios de disponibilidad de un médico para un día específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Disponibilidad configurada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping("/setup")
    @ResponseStatus(HttpStatus.CREATED)
    public AvailabilityResponseDTO create(
            @Valid @RequestBody AvailabilityRequestDTO dto
    ) {
        return agendaService.create(dto);
    }

    @Operation(summary = "Obtener agenda del médico", description = "Obtiene todos los horarios de disponibilidad de un médico específico")
    @ApiResponse(responseCode = "200", description = "Agenda obtenida exitosamente")
    @GetMapping("/doctor/{id}")
    public List<AvailabilityResponseDTO> getByDoctor(@PathVariable Long id) {
        return agendaService.getByDoctor(id);
    }
}
