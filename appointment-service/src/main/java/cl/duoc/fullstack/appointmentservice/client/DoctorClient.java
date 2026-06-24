package cl.duoc.fullstack.appointmentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="doctor-service", url="${doctor-service.url}")
public interface DoctorClient {

    @GetMapping("/api/doctors/{id}/profile")
    Object getDoctor(@PathVariable Long id);
}