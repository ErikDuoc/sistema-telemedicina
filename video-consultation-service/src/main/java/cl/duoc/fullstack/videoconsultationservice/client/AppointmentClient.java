package cl.duoc.fullstack.videoconsultationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="appointment-service", url="${appointment-service.url}")
public interface AppointmentClient {

    @GetMapping("/api/appointments/patient/{id}")
    Object validateAppointment(@PathVariable Long id);
}