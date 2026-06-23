package cl.duoc.fullstack.prescriptionservice.service;

import cl.duoc.fullstack.prescriptionservice.client.*;
import cl.duoc.fullstack.prescriptionservice.dto.*;
import cl.duoc.fullstack.prescriptionservice.exception.PrescriptionNotFoundException;
import cl.duoc.fullstack.prescriptionservice.model.Prescription;
import cl.duoc.fullstack.prescriptionservice.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrescriptionService {

    private final PrescriptionRepository repository;
    private final ClinicalRecordClient clinicalRecordClient;

    public PrescriptionResponseDTO create(PrescriptionRequestDTO request){
        log.info("Crear receta para paciente: {}, Historial clínico: {}", request.getPatientId(), request.getClinicalRecordId());

        // Validación 1: Datos no deben ser nulos
        if (request.getClinicalRecordId() == null || request.getClinicalRecordId() <= 0) {
            log.warn("ID de historial clínico inválido: {}", request.getClinicalRecordId());
            throw new IllegalArgumentException("ID de historial clínico debe ser válido");
        }

        if (request.getPatientId() == null || request.getPatientId() <= 0) {
            log.warn("ID de paciente inválido: {}", request.getPatientId());
            throw new IllegalArgumentException("ID de paciente debe ser válido");
        }

        // Validación 2: Medicamentos no pueden estar vacíos
        if (request.getMedication() == null || request.getMedication().trim().isEmpty()) {
            log.warn("Lista de medicamentos está vacía");
            throw new IllegalArgumentException("Debe especificar al menos un medicamento");
        }

        // Validación 3: Indicaciones no pueden estar vacías
        if (request.getIndications() == null || request.getIndications().trim().isEmpty()) {
            log.warn("Indicaciones están vacías");
            throw new IllegalArgumentException("Debe especificar indicaciones médicas");
        }

        // Validación 4: Verificar que el historial clínico existe
        try {
            clinicalRecordClient.getClinicalRecord(request.getClinicalRecordId());
        } catch (Exception e) {
            log.warn("Historial clínico no encontrado: {}", request.getClinicalRecordId());
            throw new IllegalArgumentException("Historial clínico no existe");
        }

        // Validación 5: Historial clínico verificado - continuar con creación
        Prescription prescription = Prescription.builder()
                .clinicalRecordId(request.getClinicalRecordId())
                .patientId(request.getPatientId())
                .medication(request.getMedication())
                .indications(request.getIndications())
                .createdAt(LocalDateTime.now().toString())
                .build();

        Prescription saved = repository.save(prescription);
        log.info("Receta creada exitosamente con ID: {}", saved.getId());

        return map(saved);
    }

    public PrescriptionResponseDTO getById(Long id){
        log.info("Recuperar receta: {}", id);

        Prescription prescription = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Receta no encontrada: {}", id);
                    return new PrescriptionNotFoundException("Receta no encontrada");
                });

        return map(prescription);
    }

    private PrescriptionResponseDTO map(Prescription p){
        return PrescriptionResponseDTO.builder()
                .id(p.getId())
                .clinicalRecordId(p.getClinicalRecordId())
                .patientId(p.getPatientId())
                .medication(p.getMedication())
                .indications(p.getIndications())
                .createdAt(p.getCreatedAt())
                .build();
    }
}