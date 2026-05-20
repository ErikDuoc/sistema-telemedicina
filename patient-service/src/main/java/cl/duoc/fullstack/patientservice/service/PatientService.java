package cl.duoc.fullstack.patientservice.service;

import cl.duoc.fullstack.patientservice.dto.*;
import cl.duoc.fullstack.patientservice.model.*;
import cl.duoc.fullstack.patientservice.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);
    private final PatientRepository patientRepository;

    public List<PatientResponseDTO> findAll() {
        return patientRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public Optional<PatientResponseDTO> findById(Long id) {
        logger.debug("Buscando paciente en base de datos con ID: {}", id);
        return patientRepository.findById(id).map(this::toResponseDTO);
    }

    @Transactional
    public PatientResponseDTO create(PatientRequestDTO dto) {
        logger.debug("Validando paciente con RUT: {} y email: {}", dto.rut(), dto.email());
        if (patientRepository.existsByRutIgnoreCase(dto.rut()) || patientRepository.existsByEmailIgnoreCase(dto.email())) {
            logger.warn("Intento de creación de paciente duplicado - RUT: {} o email: {} ya existe", dto.rut(), dto.email());
            throw new IllegalArgumentException("Paciente con RUT o email ya existe");
        }
        Patient patient = toEntity(dto);
        PatientResponseDTO saved = toResponseDTO(patientRepository.save(patient));
        logger.info("Paciente registrado en base de datos con ID: {} - RUT: {}", saved.id(), saved.rut());
        return saved;
    }

    @Transactional
    public Optional<PatientResponseDTO> update(Long id, PatientRequestDTO dto) {
        logger.debug("Buscando paciente para actualizar con ID: {}", id);
        return patientRepository.findById(id).map(existing -> {
            logger.info("Actualizando datos del paciente ID: {} - nuevo email: {}", id, dto.email());
            existing.setRut(dto.rut());
            existing.setNombre(dto.nombre());
            existing.setApellido(dto.apellido());
            existing.setFechaNacimiento(dto.fechaNacimiento());
            existing.setGenero(dto.genero());
            existing.setEmail(dto.email());
            existing.setPrevision(dto.prevision());
            // Manejo de contactos de emergencia (reemplazo completo)
            existing.getContactosEmergencia().clear();
            if (dto.contactosEmergencia() != null) {
                List<EmergencyContact> contactos = dto.contactosEmergencia().stream()
                    .map(c -> EmergencyContact.builder()
                        .nombre(c.nombre())
                        .parentesco(c.parentesco())
                        .telefono(c.telefono())
                        .patient(existing)
                        .build())
                    .collect(Collectors.toList());
                existing.getContactosEmergencia().addAll(contactos);
                logger.debug("Actualizados {} contactos de emergencia para paciente ID: {}", contactos.size(), id);
            }
            PatientResponseDTO updated = toResponseDTO(patientRepository.save(existing));
            logger.info("Datos guardados exitosamente para paciente ID: {}", id);
            return updated;
        });
    }

    @Transactional
    public boolean delete(Long id) {
        logger.debug("Buscando paciente para eliminar con ID: {}", id);
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            logger.info("Paciente eliminado de base de datos con ID: {}", id);
            return true;
        }
        logger.warn("Intento de eliminación de paciente inexistente con ID: {}", id);
        return false;
    }

    // --- Mapping helpers ---
    private PatientResponseDTO toResponseDTO(Patient patient) {
        List<EmergencyContactDTO> contactos = patient.getContactosEmergencia() == null ? List.of() :
            patient.getContactosEmergencia().stream()
                .map(c -> new EmergencyContactDTO(c.getId(), c.getNombre(), c.getParentesco(), c.getTelefono()))
                .collect(Collectors.toList());
        return new PatientResponseDTO(
            patient.getId(),
            patient.getRut(),
            patient.getNombre(),
            patient.getApellido(),
            patient.getFechaNacimiento(),
            patient.getGenero(),
            patient.getEmail(),
            patient.getPrevision(),
            contactos
        );
    }

    private Patient toEntity(PatientRequestDTO dto) {
        Patient patient = Patient.builder()
            .rut(dto.rut())
            .nombre(dto.nombre())
            .apellido(dto.apellido())
            .fechaNacimiento(dto.fechaNacimiento())
            .genero(dto.genero())
            .email(dto.email())
            .prevision(dto.prevision())
            .build();
        if (dto.contactosEmergencia() != null) {
            List<EmergencyContact> contactos = dto.contactosEmergencia().stream()
                .map(c -> EmergencyContact.builder()
                    .nombre(c.nombre())
                    .parentesco(c.parentesco())
                    .telefono(c.telefono())
                    .patient(patient)
                    .build())
                .collect(Collectors.toList());
            patient.setContactosEmergencia(contactos);
        }
        return patient;
    }
}

