package cl.duoc.fullstack.appointmentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="agenda-service", url="${agenda-service.url}")
public interface AgendaClient {

    @GetMapping("/api/agenda/doctor/{id}")
    Object getDoctorAgenda(@PathVariable Long id);
}