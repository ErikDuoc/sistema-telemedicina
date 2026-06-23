package cl.duoc.fullstack.prescriptionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="pharmacy-service", url="${pharmacy-service.url}")
public interface PharmacyClient {

    @PostMapping("/api/pharmacy/validate")
    Object validateMedication(@RequestBody Object request);
}