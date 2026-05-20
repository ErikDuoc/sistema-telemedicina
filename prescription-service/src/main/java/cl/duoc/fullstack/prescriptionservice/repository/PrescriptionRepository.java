package cl.duoc.fullstack.prescriptionservice.repository;

import cl.duoc.fullstack.prescriptionservice.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
}