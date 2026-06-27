package cl.duoc.fullstack.prescriptionservice.controller;

import cl.duoc.fullstack.prescriptionservice.config.SecurityConfig;
import cl.duoc.fullstack.prescriptionservice.dto.PrescriptionRequestDTO;
import cl.duoc.fullstack.prescriptionservice.dto.PrescriptionResponseDTO;
import cl.duoc.fullstack.prescriptionservice.exception.PrescriptionNotFoundException;
import cl.duoc.fullstack.prescriptionservice.service.PrescriptionLinkAssembler;
import cl.duoc.fullstack.prescriptionservice.service.PrescriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PrescriptionController.class)
@Import(SecurityConfig.class)
class PrescriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PrescriptionService prescriptionService;

    @MockitoBean
    private PrescriptionLinkAssembler prescriptionLinkAssembler;

    @Test
    void create_shouldReturn201() throws Exception {
        PrescriptionRequestDTO request = new PrescriptionRequestDTO();
        request.setClinicalRecordId(1L);
        request.setPatientId(1L);
        request.setMedication("Ibuprofeno 400mg");
        request.setIndications("1 comprimido cada 8 horas");

        PrescriptionResponseDTO response = PrescriptionResponseDTO.builder()
                .id(1L)
                .clinicalRecordId(1L)
                .patientId(1L)
                .medication("Ibuprofeno 400mg")
                .indications("1 comprimido cada 8 horas")
                .createdAt("2026-06-26T23:00:00")
                .build();

        when(prescriptionService.create(any(PrescriptionRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/prescriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.medication").value("Ibuprofeno 400mg"));
    }

    @Test
    void create_shouldReturn400_whenInvalidData() throws Exception {
        mockMvc.perform(post("/api/prescriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void getById_shouldReturn200_whenExists() throws Exception {
        PrescriptionResponseDTO dto = PrescriptionResponseDTO.builder()
                .id(1L)
                .clinicalRecordId(1L)
                .patientId(1L)
                .medication("Ibuprofeno 400mg")
                .indications("1 comprimido cada 8 horas")
                .createdAt("2026-06-26T23:00:00")
                .build();

        when(prescriptionService.getById(1L)).thenReturn(dto);
        when(prescriptionLinkAssembler.toModel(dto)).thenReturn(EntityModel.of(dto));

        mockMvc.perform(get("/api/prescriptions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.medication").value("Ibuprofeno 400mg"));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        when(prescriptionService.getById(99L))
                .thenThrow(new PrescriptionNotFoundException("Receta no encontrada"));

        mockMvc.perform(get("/api/prescriptions/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }
}
