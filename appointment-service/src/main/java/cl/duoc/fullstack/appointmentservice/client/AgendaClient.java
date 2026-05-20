package cl.duoc.fullstack.appointmentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="agenda-service", url="http://localhost:8085")
public interface AgendaClient {

    @GetMapping("/api/agenda/doctor/{id}")
    Object getDoctorAgenda(@PathVariable Long id);

    @PatchMapping("/api/agenda/blocks/{id}/reserve")
    void reserveBlock(@PathVariable Long id);
}