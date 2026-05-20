package cl.duoc.fullstack.videoconsultationservice.controller;

import cl.duoc.fullstack.videoconsultationservice.dto.*;
import cl.duoc.fullstack.videoconsultationservice.service.VideoConsultationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/video-consultations")
@RequiredArgsConstructor
public class VideoConsultationController {

    private final VideoConsultationService service;

    @PostMapping
    public VideoConsultationResponseDTO create(@Valid @RequestBody VideoConsultationRequestDTO request){
        return service.create(request);
    }

    @GetMapping("/{id}")
    public VideoConsultationResponseDTO getById(@PathVariable Long id){
        return service.getById(id);
    }
}