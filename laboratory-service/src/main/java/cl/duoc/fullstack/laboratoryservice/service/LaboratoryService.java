package cl.duoc.fullstack.laboratoryservice.service;

import cl.duoc.fullstack.laboratoryservice.client.NotificationClient;
import cl.duoc.fullstack.laboratoryservice.client.PatientClient;
import cl.duoc.fullstack.laboratoryservice.dto.*;
import cl.duoc.fullstack.laboratoryservice.model.*;
import cl.duoc.fullstack.laboratoryservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LaboratoryService {

    private final LabOrderRepository orderRepository;
    private final LabResultRepository resultRepository;
    private final PatientClient patientClient;
    private final NotificationClient notificationClient;

    private static final List<String> VALID_EXAM_TYPES = List.of(
            "Examen Completo de Sangre",
            "Glucosa en Ayunas",
            "Perfil Lipídico",
            "Electrocardiograma",
            "Radiografía de Tórax",
            "Ecografía",
            "Análisis de Orina",
            "Cultivo de Sangre"
    );

    public LabOrderResponseDTO createOrder(LabOrderRequestDTO dto) {
        log.info("Crear orden de examen para paciente: {}, Tipo: {}", dto.getPatientId(), dto.getExamType());

        // Validación 1: Paciente debe existir
        if (dto.getPatientId() == null || dto.getPatientId() <= 0) {
            log.warn("ID de paciente inválido: {}", dto.getPatientId());
            throw new IllegalArgumentException("ID de paciente es inválido");
        }

        try {
            PatientResponseDTO patient = patientClient.getPatient(dto.getPatientId());
            log.info("Paciente validado: {}", patient.getLastName());
        } catch (Exception e) {
            log.warn("Paciente no encontrado: {}", dto.getPatientId());
            throw new IllegalArgumentException("El paciente no existe");
        }

        // Validación 2: Tipo de examen no puede estar vacío
        if (dto.getExamType() == null || dto.getExamType().trim().isEmpty()) {
            log.warn("Tipo de examen no especificado");
            throw new IllegalArgumentException("Debe especificar un tipo de examen");
        }

        // Validación 3: Validar tipo de examen (opcional - para registro)
        if (!VALID_EXAM_TYPES.contains(dto.getExamType())) {
            log.warn("Tipo de examen no estándar (pero permitido): {}", dto.getExamType());
            // No falla, solo registra - permite nuevos tipos
        }

        // Validación 4: Doctor debe ser especificado
        if (dto.getDoctorId() == null || dto.getDoctorId() <= 0) {
            log.warn("ID de doctor inválido: {}", dto.getDoctorId());
            throw new IllegalArgumentException("ID de doctor es inválido");
        }

        LabOrder order = LabOrder.builder()
                .patientId(dto.getPatientId())
                .doctorId(dto.getDoctorId())
                .examType(dto.getExamType())
                .status("PENDIENTE")
                .build();

        LabOrder saved = orderRepository.save(order);
        log.info("Orden de examen creada con ID: {}", saved.getId());

        return LabOrderResponseDTO.builder()
                .id(saved.getId())
                .patientId(saved.getPatientId())
                .patientName(dto.getPatientId().toString()) // Placeholder si no viene del cliente
                .doctorId(saved.getDoctorId())
                .examType(saved.getExamType())
                .status(saved.getStatus())
                .build();
    }

    public void uploadResult(Long orderId, ResultRequestDTO dto) {
        log.info("Subir resultado para orden: {}", orderId);

        // Validación 1: La orden debe existir
        LabOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Orden de examen no encontrada: {}", orderId);
                    return new RuntimeException("Orden de examen no encontrada");
                });

        // Validación 2: No se pueden subir resultados a orden cancelada
        if (order.getStatus().equals("CANCELADA")) {
            log.warn("Intento de subir resultado a orden cancelada: {}", orderId);
            throw new IllegalArgumentException("No se pueden subir resultados a una orden cancelada");
        }

        // Validación 3: Hallazgos no pueden estar vacíos
        if (dto.getFindings() == null || dto.getFindings().trim().isEmpty()) {
            log.warn("Hallazgos están vacíos para orden: {}", orderId);
            throw new IllegalArgumentException("Debe incluir los hallazgos del examen");
        }

        LabResult result = LabResult.builder()
                .orderId(orderId)
                .findings(dto.getFindings())
                .documentUrl(dto.getDocumentUrl())
                .build();

        resultRepository.save(result);

        order.setStatus("COMPLETADO");
        orderRepository.save(order);
        log.info("Resultado guardado para orden: {}", orderId);

        // Notificar paciente (con fallback graceful)
        try {
            PatientResponseDTO patient = patientClient.getPatient(order.getPatientId());

            NotificationRequestDTO notification = NotificationRequestDTO.builder()
                    .recipientId(patient.getId())
                    .type("EMAIL")
                    .recipient(patient.getEmail())
                    .message("Sus resultados de laboratorio se encuentran disponibles.")
                    .build();

            notificationClient.sendNotification(notification);
            log.info("Notificación enviada al paciente: {}", order.getPatientId());
        } catch (Exception e) {
            log.warn("No se pudo enviar notificación al paciente: {}", e.getMessage());
            // No fallar si notificación falla - resiliencia
        }
    }

    public List<LabOrderResponseDTO> getPatientOrders(Long patientId) {
        log.info("Recuperar órdenes de examen para paciente: {}", patientId);

        PatientResponseDTO patient;
        try {
            patient = patientClient.getPatient(patientId);
        } catch (Exception e) {
            log.warn("Paciente no encontrado: {}", patientId);
            throw new IllegalArgumentException("El paciente no existe");
        }

        String patientName = patient.getFirstName() + " " + patient.getLastName();

        return orderRepository.findByPatientId(patientId).stream()
                .map(order -> LabOrderResponseDTO.builder()
                        .id(order.getId())
                        .patientId(order.getPatientId())
                        .patientName(patientName)
                        .doctorId(order.getDoctorId())
                        .examType(order.getExamType())
                        .status(order.getStatus())
                        .build())
                .toList();
    }
}
