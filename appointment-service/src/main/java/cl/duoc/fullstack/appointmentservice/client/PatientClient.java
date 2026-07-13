package cl.duoc.fullstack.appointmentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="patient-service", fallback = PatientClientFallback.class)
public interface PatientClient {

    @GetMapping("/api/patients/{id}")
    Object getPatient(@PathVariable Long id);
}