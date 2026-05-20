package cl.duoc.fullstack.appointmentservice.service;

import cl.duoc.fullstack.appointmentservice.client.*;
import cl.duoc.fullstack.appointmentservice.dto.*;
import cl.duoc.fullstack.appointmentservice.exception.AppointmentNotFoundException;
import cl.duoc.fullstack.appointmentservice.exception.DuplicateAppointmentException;
import cl.duoc.fullstack.appointmentservice.model.Appointment;
import cl.duoc.fullstack.appointmentservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository repository;
    private final PatientClient patientClient;
    private final DoctorClient doctorClient;
    private final AgendaClient agendaClient;

    // Estados válidos para una cita
    private static final List<String> VALID_STATUSES = List.of("PENDING", "CONFIRMED", "CANCELLED", "COMPLETED");

    public AppointmentResponseDTO create(AppointmentRequest request){
        log.info("Crear cita: Paciente {} con Doctor {} para {}", request.getPatientId(), request.getDoctorId(), request.getDate());

        // Validación 1: Verificar que paciente existe
        try {
            patientClient.getPatient(request.getPatientId());
        } catch (Exception e) {
            log.warn("Paciente {} no encontrado", request.getPatientId());
            throw new IllegalArgumentException("El paciente no existe");
        }

        // Validación 2: Verificar que doctor existe
        try {
            doctorClient.getDoctor(request.getDoctorId());
        } catch (Exception e) {
            log.warn("Doctor {} no encontrado", request.getDoctorId());
            throw new IllegalArgumentException("El doctor no existe");
        }

        // Validación 3: Verificar que doctor tiene agenda
        try {
            agendaClient.getDoctorAgenda(request.getDoctorId());
        } catch (Exception e) {
            log.warn("Doctor {} no tiene agenda configurada", request.getDoctorId());
            throw new IllegalArgumentException("El doctor no tiene agenda disponible");
        }

        // Validación 4: Evitar fechas pasadas
        LocalDate appointmentDate = LocalDate.parse(request.getDate());
        if (appointmentDate.isBefore(LocalDate.now())) {
            log.warn("Intento de reservar fecha pasada: {}", request.getDate());
            throw new IllegalArgumentException("No se pueden reservar citas en fechas pasadas");
        }

        // Validación 5: Evitar doble reserva (mismo paciente, mismo doctor, mismo día y hora)
        boolean existingAppointment = repository.findByPatientId(request.getPatientId())
                .stream()
                .anyMatch(apt -> apt.getDoctorId().equals(request.getDoctorId())
                        && apt.getDate().equals(request.getDate())
                        && apt.getTime().equals(request.getTime())
                        && !apt.getStatus().equals("CANCELLED"));

        if (existingAppointment) {
            log.warn("Intento de doble reserva: Paciente {} con Doctor {} en {}",
                      request.getPatientId(), request.getDoctorId(), request.getDate());
            throw new DuplicateAppointmentException("Ya existe una cita para este paciente, doctor y horario");
        }

        // Crear la cita
        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .date(request.getDate())
                .time(request.getTime())
                .status("PENDING")
                .build();

        Appointment saved = repository.save(appointment);
        log.info("Cita creada exitosamente con ID: {}", saved.getId());

        return map(saved);
    }

    public List<Appointment> getByPatient(Long id){
        log.info("Recuperar citas para paciente: {}", id);
        return repository.findByPatientId(id);
    }

    public AppointmentResponseDTO updateStatus(Long id, String status){
        log.info("Actualizar estado de cita {} a: {}", id, status);

        // Validación: Verificar que el estado es válido
        if (!VALID_STATUSES.contains(status)) {
            log.warn("Estado inválido: {}. Estados válidos: {}", status, VALID_STATUSES);
            throw new IllegalArgumentException("Estado de cita inválido. Estados válidos: " + VALID_STATUSES);
        }

        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cita no encontrada: {}", id);
                    return new AppointmentNotFoundException("Cita no encontrada");
                });

        appointment.setStatus(status);
        Appointment updated = repository.save(appointment);
        log.info("Estado de cita actualizado: {}", id);

        return map(updated);
    }

    private AppointmentResponseDTO map(Appointment appointment){
        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .date(appointment.getDate())
                .time(appointment.getTime())
                .status(appointment.getStatus())
                .build();
    }
}