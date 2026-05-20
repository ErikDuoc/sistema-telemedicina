package cl.duoc.fullstack.laboratoryservice.repository;

import cl.duoc.fullstack.laboratoryservice.model.LabOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabOrderRepository extends JpaRepository<LabOrder, Long> {

    List<LabOrder> findByPatientId(Long patientId);
}
