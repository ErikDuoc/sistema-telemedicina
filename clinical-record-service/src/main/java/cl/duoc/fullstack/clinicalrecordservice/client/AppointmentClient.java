package cl.duoc.fullstack.clinicalrecordservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="appointment-service", fallback = AppointmentClientFallback.class)
public interface AppointmentClient {

    @GetMapping("/api/appointments/patient/{id}")
    Object validateAppointment(@PathVariable Long id);
}