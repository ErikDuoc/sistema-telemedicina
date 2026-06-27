package cl.duoc.fullstack.clinicalrecordservice.repository;

import cl.duoc.fullstack.clinicalrecordservice.model.ClinicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicalRecordRepository extends JpaRepository<ClinicalRecord, Long> {
    java.util.Optional<ClinicalRecord> findByAppointmentId(Long appointmentId);
}