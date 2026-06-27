package cl.duoc.fullstack.clinicalrecordservice.controller;

import cl.duoc.fullstack.clinicalrecordservice.config.SecurityConfig;
import cl.duoc.fullstack.clinicalrecordservice.dto.ClinicalRecordRequestDTO;
import cl.duoc.fullstack.clinicalrecordservice.dto.ClinicalRecordResponseDTO;
import cl.duoc.fullstack.clinicalrecordservice.exception.ClinicalRecordNotFoundException;
import cl.duoc.fullstack.clinicalrecordservice.service.ClinicalRecordLinkAssembler;
import cl.duoc.fullstack.clinicalrecordservice.service.ClinicalRecordService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClinicalRecordController.class)
@Import(SecurityConfig.class)
class ClinicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClinicalRecordService service;

    @MockitoBean
    private ClinicalRecordLinkAssembler assembler;

    @Test
    void create_shouldReturn201() throws Exception {
        ClinicalRecordRequestDTO request = new ClinicalRecordRequestDTO();
        request.setAppointmentId(1L);
        request.setPatientId(5L);
        request.setDoctorId(3L);
        request.setDiagnosis("Hipertensión arterial");
        request.setTreatment("Medicación antihipertensiva");

        ClinicalRecordResponseDTO response = ClinicalRecordResponseDTO.builder()
                .id(1L)
                .appointmentId(1L)
                .patientId(5L)
                .doctorId(3L)
                .diagnosis("Hipertensión arterial")
                .treatment("Medicación antihipertensiva")
                .createdAt("2025-01-01T10:00:00")
                .build();

        when(service.create(any(ClinicalRecordRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/clinical-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.appointmentId").value(1L))
                .andExpect(jsonPath("$.diagnosis").value("Hipertensión arterial"));
    }

    @Test
    void getById_shouldReturn200() throws Exception {
        ClinicalRecordResponseDTO response = ClinicalRecordResponseDTO.builder()
                .id(1L)
                .appointmentId(1L)
                .patientId(5L)
                .doctorId(3L)
                .diagnosis("Hipertensión arterial")
                .treatment("Medicación antihipertensiva")
                .createdAt("2025-01-01T10:00:00")
                .build();

        when(service.getById(1L)).thenReturn(response);
        when(assembler.toModel(response)).thenReturn(EntityModel.of(response));

        mockMvc.perform(get("/api/clinical-records/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.appointmentId").value(1L));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        when(service.getById(99L)).thenThrow(new ClinicalRecordNotFoundException("Clinical record not found"));

        mockMvc.perform(get("/api/clinical-records/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Clinical record not found"));
    }

    @Test
    void create_shouldReturn400_whenInvalidData() throws Exception {
        ClinicalRecordRequestDTO request = new ClinicalRecordRequestDTO();

        mockMvc.perform(post("/api/clinical-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
