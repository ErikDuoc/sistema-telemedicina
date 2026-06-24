package cl.duoc.fullstack.agendaservice.controller;

import cl.duoc.fullstack.agendaservice.dto.AvailabilityRequestDTO;
import cl.duoc.fullstack.agendaservice.dto.AvailabilityResponseDTO;
import cl.duoc.fullstack.agendaservice.service.AgendaService;
import cl.duoc.fullstack.agendaservice.service.AvailabilityLinkAssembler;
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

@Tag(name = "Agenda y Disponibilidad", description = "Operaciones para gestionar agendas y disponibilidad de médicos")
@RestController
@RequestMapping("/api/agenda")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;
    private final AvailabilityLinkAssembler availabilityLinkAssembler;

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

    @Operation(summary = "Obtener agenda del médico", description = "Obtiene todos los horarios de disponibilidad de un médico específico con enlaces HATEOAS en _links")
    @ApiResponse(responseCode = "200", description = "Agenda obtenida exitosamente")
    @GetMapping("/doctor/{id}")
    public ResponseEntity<CollectionModel<EntityModel<AvailabilityResponseDTO>>> getByDoctor(@PathVariable Long id) {
        List<EntityModel<AvailabilityResponseDTO>> availabilities = agendaService.getByDoctor(id).stream()
                .map(availabilityLinkAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<AvailabilityResponseDTO>> collection = CollectionModel.of(availabilities);
        collection.add(linkTo(methodOn(AgendaController.class).getByDoctor(id)).withSelfRel());

        return ResponseEntity.ok(collection);
    }
}
