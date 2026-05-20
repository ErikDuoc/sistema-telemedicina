package cl.duoc.fullstack.laboratoryservice.controller;

import cl.duoc.fullstack.laboratoryservice.dto.*;
import cl.duoc.fullstack.laboratoryservice.model.LabOrder;
import cl.duoc.fullstack.laboratoryservice.service.LaboratoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lab")
@RequiredArgsConstructor
public class LaboratoryController {

    private final LaboratoryService laboratoryService;

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public LabOrderResponseDTO createOrder(
            @Valid @RequestBody LabOrderRequestDTO dto
    ) {
        return laboratoryService.createOrder(dto);
    }

    @PutMapping("/results/{orderId}")
    public String uploadResult(
            @PathVariable Long orderId,
            @Valid @RequestBody ResultRequestDTO dto
    ) {

        laboratoryService.uploadResult(orderId, dto);

        return "Result uploaded successfully";
    }

    @GetMapping("/patient/{id}")
    public List<LabOrder> getPatientOrders(@PathVariable Long id) {

        return laboratoryService.getPatientOrders(id);
    }
}
