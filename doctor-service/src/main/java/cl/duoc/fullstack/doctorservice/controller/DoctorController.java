package cl.duoc.fullstack.doctorservice.controller;

import cl.duoc.fullstack.doctorservice.dto.DoctorRequestDTO;
import cl.duoc.fullstack.doctorservice.dto.DoctorResponseDTO;
import cl.duoc.fullstack.doctorservice.service.DoctorLinkAssembler;
import cl.duoc.fullstack.doctorservice.service.DoctorService;
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

//Se agrega tag para swagger
@Tag(name = "Doctores", description = "Operaciones para gestionar doctores")
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorLinkAssembler doctorLinkAssembler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorResponseDTO createDoctor(@Valid @RequestBody DoctorRequestDTO dto) {

        return doctorService.createDoctor(dto);
    }

    //Se agrega operation y apiResponse para swagger
    @Operation(summary = "Listar doctores", description = "Obtiene todos los doctores registrados con enlaces HATEOAS en _links")
    @ApiResponse(responseCode = "200", description = "Doctores obtenidos correctamente")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<DoctorResponseDTO>>> getAllDoctors() {
        List<EntityModel<DoctorResponseDTO>> doctors = doctorService.getAllDoctors().stream()
                .map(doctorLinkAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<DoctorResponseDTO>> collection = CollectionModel.of(doctors);
        collection.add(linkTo(methodOn(DoctorController.class).getAllDoctors()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    //Se agrega operation y apiResponses para swagger
    @Operation(summary = "Buscar doctor por id", description = "Obtiene un doctor específico con enlaces HATEOAS en _links")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Doctor encontrado"),
            @ApiResponse(responseCode = "404", description = "Doctor no encontrado")
    })
    @GetMapping("/{id}/profile")
    public ResponseEntity<EntityModel<DoctorResponseDTO>> getDoctorById(@PathVariable Long id) {

        DoctorResponseDTO doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(doctorLinkAssembler.toModel(doctor));
    }
}
