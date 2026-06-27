package cl.duoc.fullstack.agendaservice.repository;

import cl.duoc.fullstack.agendaservice.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByDoctorId(Long doctorId);

    Optional<Availability> findByDoctorIdAndDayOfWeek(Long doctorId, String dayOfWeek);
}
