package cl.duoc.fullstack.clinicalrecordservice.service;

import cl.duoc.fullstack.clinicalrecordservice.client.AppointmentClient;
import cl.duoc.fullstack.clinicalrecordservice.dto.*;
import cl.duoc.fullstack.clinicalrecordservice.exception.ClinicalRecordNotFoundException;
import cl.duoc.fullstack.clinicalrecordservice.exception.DuplicateClinicalRecordException;
import cl.duoc.fullstack.clinicalrecordservice.model.*;
import cl.duoc.fullstack.clinicalrecordservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClinicalRecordService {

    private final ClinicalRecordRepository repository;
    private final AppointmentClient appointmentClient;

    public ClinicalRecordResponseDTO create(ClinicalRecordRequestDTO request){
        log.info("Crear historial clínico para paciente: {}, cita: {}", request.getPatientId(), request.getAppointmentId());

        appointmentClient.validateAppointment(request.getPatientId());

        if (repository.findByAppointmentId(request.getAppointmentId()).isPresent()) {
            throw new DuplicateClinicalRecordException("A clinical record already exists for appointment " + request.getAppointmentId());
        }

        ClinicalRecord record = ClinicalRecord.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .diagnosis(request.getDiagnosis())
                .treatment(request.getTreatment())
                .createdAt(LocalDateTime.now().toString())
                .build();

        ClinicalRecord saved = repository.save(record);
        log.info("✅ Historial clínico creado: {}", saved.getId());

        return map(saved);
    }

    public ClinicalRecordResponseDTO getById(Long id){
        log.info("Recuperar historial clínico: {}", id);

        ClinicalRecord record = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Historial clínico no encontrado: {}", id);
                    return new ClinicalRecordNotFoundException("Clinical record not found");
                });

        return map(record);
    }

    private ClinicalRecordResponseDTO map(ClinicalRecord record){
        return ClinicalRecordResponseDTO.builder()
                .id(record.getId())
                .appointmentId(record.getAppointmentId())
                .patientId(record.getPatientId())
                .doctorId(record.getDoctorId())
                .diagnosis(record.getDiagnosis())
                .treatment(record.getTreatment())
                .createdAt(record.getCreatedAt())
                .build();
    }
}