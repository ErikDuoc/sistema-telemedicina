package cl.duoc.fullstack.laboratoryservice.client;

import cl.duoc.fullstack.laboratoryservice.dto.PatientResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service", fallback = PatientClientFallback.class)
public interface PatientClient {

    @GetMapping("/api/patients/{id}")
    PatientResponseDTO getPatient(@PathVariable Long id);
}
