package cl.duoc.fullstack.appointmentservice.controller;

import cl.duoc.fullstack.appointmentservice.dto.*;
import cl.duoc.fullstack.appointmentservice.model.Appointment;
import cl.duoc.fullstack.appointmentservice.service.AppointmentService;
import cl.duoc.fullstack.appointmentservice.service.AppointmentLinkAssembler;
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

@Tag(name = "Citas Médicas", description = "Operaciones para gestionar citas médicas")
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;
    private final AppointmentLinkAssembler appointmentLinkAssembler;

    @Operation(summary = "Crear cita médica", description = "Crea una nueva cita médica entre un paciente y un médico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cita creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponseDTO create(@Valid @RequestBody AppointmentRequest request){
        return service.create(request);
    }

    @Operation(summary = "Obtener citas del paciente", description = "Obtiene todas las citas médicas de un paciente específico con enlaces HATEOAS en _links")
    @ApiResponse(responseCode = "200", description = "Citas obtenidas exitosamente")
    @GetMapping("/patient/{id}")
    public ResponseEntity<CollectionModel<EntityModel<AppointmentResponseDTO>>> getByPatient(@PathVariable Long id){
        List<EntityModel<AppointmentResponseDTO>> appointments = service.getByPatient(id).stream()
                .map(apt -> appointmentLinkAssembler.toModel(
                    AppointmentResponseDTO.builder()
                        .id(apt.getId())
                        .patientId(apt.getPatientId())
                        .doctorId(apt.getDoctorId())
                        .date(apt.getDate())
                        .time(apt.getTime())
                        .status(apt.getStatus())
                        .build()
                ))
                .toList();

        CollectionModel<EntityModel<AppointmentResponseDTO>> collection = CollectionModel.of(appointments);
        collection.add(linkTo(methodOn(AppointmentController.class).getByPatient(id)).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Actualizar estado de cita", description = "Cambia el estado de una cita médica (confirmada, cancelada, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Cita no encontrada")
    })
    @PatchMapping("/{id}/status")
    public AppointmentResponseDTO updateStatus(@PathVariable Long id,
                                               @RequestParam String status){
        return service.updateStatus(id, status);
    }
}