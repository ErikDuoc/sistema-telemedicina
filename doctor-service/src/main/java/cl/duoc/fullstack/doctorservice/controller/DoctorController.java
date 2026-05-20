package cl.duoc.fullstack.doctorservice.controller;

import cl.duoc.fullstack.doctorservice.dto.DoctorRequestDTO;
import cl.duoc.fullstack.doctorservice.dto.DoctorResponseDTO;
import cl.duoc.fullstack.doctorservice.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorResponseDTO createDoctor(@Valid @RequestBody DoctorRequestDTO dto) {

        return doctorService.createDoctor(dto);
    }

    @GetMapping
    public List<DoctorResponseDTO> getAllDoctors() {

        return doctorService.getAllDoctors();
    }

    @GetMapping("/{id}/profile")
    public DoctorResponseDTO getDoctorById(@PathVariable Long id) {

        return doctorService.getDoctorById(id);
    }
}
