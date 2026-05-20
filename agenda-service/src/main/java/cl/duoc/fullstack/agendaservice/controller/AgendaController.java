package cl.duoc.fullstack.agendaservice.controller;

import cl.duoc.fullstack.agendaservice.dto.AvailabilityRequestDTO;
import cl.duoc.fullstack.agendaservice.dto.AvailabilityResponseDTO;
import cl.duoc.fullstack.agendaservice.service.AgendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agenda")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @PostMapping("/setup")
    @ResponseStatus(HttpStatus.CREATED)
    public AvailabilityResponseDTO create(
            @Valid @RequestBody AvailabilityRequestDTO dto
    ) {
        return agendaService.create(dto);
    }

    @GetMapping("/doctor/{id}")
    public List<AvailabilityResponseDTO> getByDoctor(@PathVariable Long id) {
        return agendaService.getByDoctor(id);
    }
}
