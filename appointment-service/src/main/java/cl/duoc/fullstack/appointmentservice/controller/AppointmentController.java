package cl.duoc.fullstack.appointmentservice.controller;

import cl.duoc.fullstack.appointmentservice.dto.*;
import cl.duoc.fullstack.appointmentservice.model.Appointment;
import cl.duoc.fullstack.appointmentservice.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @PostMapping
    public AppointmentResponseDTO create(@Valid @RequestBody AppointmentRequest request){
        return service.create(request);
    }

    @GetMapping("/patient/{id}")
    public List<Appointment> getByPatient(@PathVariable Long id){
        return service.getByPatient(id);
    }

    @PatchMapping("/{id}/status")
    public AppointmentResponseDTO updateStatus(@PathVariable Long id,
                                               @RequestParam String status){
        return service.updateStatus(id, status);
    }
}