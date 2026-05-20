package cl.duoc.fullstack.patientservice.controller;

import cl.duoc.fullstack.patientservice.dto.PatientRequestDTO;
import cl.duoc.fullstack.patientservice.dto.PatientResponseDTO;
import cl.duoc.fullstack.patientservice.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);
    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> findAll() {
        return ResponseEntity.ok(patientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> findById(@PathVariable Long id) {
        logger.info("Buscando paciente con ID: {}", id);
        return patientService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Paciente no encontrado con ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequestDTO request) {

        logger.info("Actualizando paciente con ID: {}", id);
        return patientService.update(id, request)
                .map(updated -> {
                    logger.info("Paciente actualizado correctamente con ID: {}", id);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> {
                    logger.warn("No se encontró paciente para actualizar con ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        logger.info("Eliminando paciente con ID: {}", id);
        boolean deleted = patientService.delete(id);
        if (deleted) {
            logger.info("Paciente eliminado correctamente con ID: {}", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("No se encontró paciente para eliminar con ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}