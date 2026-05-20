package cl.duoc.fullstack.clinicalrecordservice.controller;

import cl.duoc.fullstack.clinicalrecordservice.dto.*;
import cl.duoc.fullstack.clinicalrecordservice.service.ClinicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clinical-records")
@RequiredArgsConstructor
public class ClinicalRecordController {

    private final ClinicalRecordService service;

    @PostMapping
    public ClinicalRecordResponseDTO create(@Valid @RequestBody ClinicalRecordRequestDTO request){
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ClinicalRecordResponseDTO getById(@PathVariable Long id){
        return service.getById(id);
    }
}