package cl.duoc.fullstack.patientservice.controller;

import cl.duoc.fullstack.patientservice.dto.PatientRequestDTO;
import cl.duoc.fullstack.patientservice.dto.PatientResponseDTO;
import cl.duoc.fullstack.patientservice.service.PatientLinkAssembler;
import cl.duoc.fullstack.patientservice.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Pacientes", description = "Operaciones para gestionar pacientes")
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);
    private final PatientService patientService;
    private final PatientLinkAssembler patientLinkAssembler;

    @Operation(summary = "Obtener todos los pacientes", description = "Retorna una lista con todos los pacientes registrados en el sistema con enlaces HATEOAS en _links")
    @ApiResponse(responseCode = "200", description = "Lista de pacientes obtenida exitosamente")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PatientResponseDTO>>> findAll() {
        List<EntityModel<PatientResponseDTO>> patients = patientService.findAll().stream()
                .map(patientLinkAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<PatientResponseDTO>> collection = CollectionModel.of(patients);
        collection.add(linkTo(methodOn(PatientController.class).findAll()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Obtener paciente por ID", description = "Busca y retorna un paciente específico según su identificador con enlaces HATEOAS en _links")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente encontrado"),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PatientResponseDTO>> findById(@PathVariable Long id) {
        logger.info("Buscando paciente con ID: {}", id);
        return ResponseEntity.ok(patientLinkAssembler.toModel(patientService.findById(id)));
    }

    @Operation(summary = "Crear nuevo paciente", description = "Crea un nuevo paciente con datos personales y contactos de emergencia")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paciente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<PatientResponseDTO> create(
            @Valid @RequestBody PatientRequestDTO request) {

        logger.info("Iniciando creación de paciente con email: {}", request.email());
        PatientResponseDTO created = patientService.create(request);
        logger.info("Paciente creado correctamente con ID: {}", created.id());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @Operation(summary = "Actualizar paciente", description = "Actualiza los datos de un paciente existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paciente actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequestDTO request) {

        logger.info("Actualizando paciente con ID: {}", id);
        PatientResponseDTO updated = patientService.update(id, request);
        logger.info("Paciente actualizado correctamente con ID: {}", id);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Eliminar paciente", description = "Elimina un paciente del sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Paciente eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Paciente no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        logger.info("Eliminando paciente con ID: {}", id);
        patientService.delete(id);
        logger.info("Paciente eliminado correctamente con ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}