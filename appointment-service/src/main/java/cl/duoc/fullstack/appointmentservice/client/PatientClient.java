package cl.duoc.fullstack.appointmentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="patient-service", url="${patient-service.url}")
public interface PatientClient {

    @GetMapping("/api/patients/{id}")
    Object getPatient(@PathVariable Long id);
}