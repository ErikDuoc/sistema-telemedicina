package cl.duoc.fullstack.prescriptionservice.controller;

import cl.duoc.fullstack.prescriptionservice.dto.*;
import cl.duoc.fullstack.prescriptionservice.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService service;

    @PostMapping
    public PrescriptionResponseDTO create(@Valid @RequestBody PrescriptionRequestDTO request){
        return service.create(request);
    }

    @GetMapping("/{id}")
    public PrescriptionResponseDTO getById(@PathVariable Long id){
        return service.getById(id);
    }
}