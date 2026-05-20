package cl.duoc.fullstack.doctorservice.repository;

import cl.duoc.fullstack.doctorservice.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    boolean existsByNationalRegistry(String nationalRegistry);
}
