package cl.duoc.fullstack.prescriptionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="clinical-record-service", fallback = ClinicalRecordClientFallback.class)
public interface ClinicalRecordClient {

    @GetMapping("/api/clinical-records/{id}")
    Object getClinicalRecord(@PathVariable Long id);
}