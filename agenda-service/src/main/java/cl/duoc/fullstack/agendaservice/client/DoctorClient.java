package cl.duoc.fullstack.agendaservice.client;

import cl.duoc.fullstack.agendaservice.dto.DoctorResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "doctor-service", url = "${doctor-service.url}")
public interface DoctorClient {

    @GetMapping("/api/doctors/{id}/profile")
    DoctorResponseDTO getDoctor(@PathVariable Long id);
}
