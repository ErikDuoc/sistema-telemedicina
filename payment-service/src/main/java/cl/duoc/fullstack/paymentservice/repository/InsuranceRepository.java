package cl.duoc.fullstack.paymentservice.repository;

import cl.duoc.fullstack.paymentservice.model.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceRepository extends JpaRepository<Insurance, Long> {
}
