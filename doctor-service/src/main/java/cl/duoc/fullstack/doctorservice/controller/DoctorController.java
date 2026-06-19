package cl.duoc.fullstack.doctorservice.controller;

import cl.duoc.fullstack.doctorservice.dto.DoctorRequestDTO;
import cl.duoc.fullstack.doctorservice.dto.DoctorResponseDTO;
import cl.duoc.fullstack.doctorservice.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Se agrega tag para swagger
@Tag(name = "Doctores", description = "Operaciones para gestionar doctores")
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

    //Se agrega operation y apiResponse para swagger
    @Operation(summary = "Listar doctores", description = "Obtiene todos los doctores registrados")
    @ApiResponse(responseCode = "200", description = "Doctores obtenidos correctamente")
    @GetMapping
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    //Se agrega operation y apiResponses para swagger
    @Operation(summary = "Buscar doctor por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Doctor encontrado"),
            @ApiResponse(responseCode = "404", description = "Doctor no encontrado")
    })
    @GetMapping("/{id}/profile")
    public ResponseEntity<DoctorResponseDTO> getDoctorById(@PathVariable Long id) {

        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }
}
