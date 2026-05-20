package cl.duoc.fullstack.doctorservice.repository;

import cl.duoc.fullstack.doctorservice.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
}
