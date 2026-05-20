package cl.duoc.fullstack.laboratoryservice.repository;

import cl.duoc.fullstack.laboratoryservice.model.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabResultRepository extends JpaRepository<LabResult, Long> {
}
