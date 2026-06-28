package cl.duoc.fullstack.patientservice.controller;

import cl.duoc.fullstack.patientservice.config.SecurityConfig;
import cl.duoc.fullstack.patientservice.dto.*;
import cl.duoc.fullstack.patientservice.exception.DuplicateResourceException;
import cl.duoc.fullstack.patientservice.exception.ResourceNotFoundException;
import cl.duoc.fullstack.patientservice.service.PatientLinkAssembler;
import cl.duoc.fullstack.patientservice.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@Import(SecurityConfig.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private PatientLinkAssembler patientLinkAssembler;

    @Test
    void findAll_shouldReturn200() throws Exception {
        PatientResponseDTO patient = new PatientResponseDTO(
                1L, "20.123.456-7", "Carlos", "González Martínez",
                LocalDate.of(1990, 5, 15), "Masculino",
                "carlos@email.com", "FONASA", List.of()
        );

        when(patientService.findAll()).thenReturn(List.of(patient));
        when(patientLinkAssembler.toModel(patient)).thenReturn(EntityModel.of(patient));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.patientResponseDTOList[0].id").value(1L))
                .andExpect(jsonPath("$._embedded.patientResponseDTOList[0].nombre").value("Carlos"));
    }

    @Test
    void findById_shouldReturn200_whenExists() throws Exception {
        PatientResponseDTO patient = new PatientResponseDTO(
                1L, "20.123.456-7", "Carlos", "González Martínez",
                LocalDate.of(1990, 5, 15), "Masculino",
                "carlos@email.com", "FONASA", List.of()
        );

        when(patientService.findById(1L)).thenReturn(patient);
        when(patientLinkAssembler.toModel(patient)).thenReturn(EntityModel.of(patient));

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Carlos"));
    }

    @Test
    void findById_shouldReturn404_whenNotFound() throws Exception {
        when(patientService.findById(99L)).thenThrow(new ResourceNotFoundException("Patient", "id", 99L));

        mockMvc.perform(get("/api/patients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void create_shouldReturn201() throws Exception {
        PatientRequestDTO request = new PatientRequestDTO(
                "20.123.456-7", "Carlos", "González Martínez",
                LocalDate.of(1990, 5, 15), "Masculino",
                "carlos@email.com", "FONASA",
                List.of(new EmergencyContactDTO(null, "María González", "Madre", "+56912345678"))
        );

        PatientResponseDTO response = new PatientResponseDTO(
                1L, "20.123.456-7", "Carlos", "González Martínez",
                LocalDate.of(1990, 5, 15), "Masculino",
                "carlos@email.com", "FONASA",
                List.of(new EmergencyContactDTO(1L, "María González", "Madre", "+56912345678"))
        );

        when(patientService.create(any(PatientRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Carlos"));
    }

    @Test
    void create_shouldReturn400_whenInvalidData() throws Exception {
        PatientRequestDTO request = new PatientRequestDTO(
                null, null, null, null, null, null, null, null
        );

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn409_whenDuplicateRut() throws Exception {
        PatientRequestDTO request = new PatientRequestDTO(
                "20.123.456-7", "Carlos", "González Martínez",
                LocalDate.of(1990, 5, 15), "Masculino",
                "carlos@email.com", "FONASA",
                List.of(new EmergencyContactDTO(null, "María González", "Madre", "+56912345678"))
        );

        when(patientService.create(any(PatientRequestDTO.class)))
                .thenThrow(new DuplicateResourceException("Ya existe un paciente con el RUT: 20.123.456-7"));

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DUPLICATE_RESOURCE"));
    }

    @Test
    void update_shouldReturn200() throws Exception {
        PatientRequestDTO request = new PatientRequestDTO(
                "20.123.456-7", "Carlos", "González Martínez",
                LocalDate.of(1990, 5, 15), "Masculino",
                "carlos@email.com", "FONASA",
                List.of(new EmergencyContactDTO(null, "María González", "Madre", "+56912345678"))
        );

        PatientResponseDTO response = new PatientResponseDTO(
                1L, "20.123.456-7", "Carlos", "González Martínez",
                LocalDate.of(1990, 5, 15), "Masculino",
                "carlos@email.com", "FONASA",
                List.of(new EmergencyContactDTO(1L, "María González", "Madre", "+56912345678"))
        );

        when(patientService.update(eq(1L), any(PatientRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Carlos"));
    }

    @Test
    void update_shouldReturn404_whenNotFound() throws Exception {
        PatientRequestDTO request = new PatientRequestDTO(
                "20.123.456-7", "Carlos", "González Martínez",
                LocalDate.of(1990, 5, 15), "Masculino",
                "carlos@email.com", "FONASA",
                List.of(new EmergencyContactDTO(null, "María González", "Madre", "+56912345678"))
        );

        when(patientService.update(eq(99L), any(PatientRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Patient", "id", 99L));

        mockMvc.perform(put("/api/patients/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Patient", "id", 99L))
                .when(patientService).delete(99L);

        mockMvc.perform(delete("/api/patients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }
}
