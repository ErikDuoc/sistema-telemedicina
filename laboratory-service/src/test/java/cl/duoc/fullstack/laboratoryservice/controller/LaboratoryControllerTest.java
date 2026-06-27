package cl.duoc.fullstack.laboratoryservice.controller;

import cl.duoc.fullstack.laboratoryservice.config.SecurityConfig;
import cl.duoc.fullstack.laboratoryservice.dto.*;
import cl.duoc.fullstack.laboratoryservice.service.LaboratoryLinkAssembler;
import cl.duoc.fullstack.laboratoryservice.service.LaboratoryService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LaboratoryController.class)
@Import(SecurityConfig.class)
class LaboratoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LaboratoryService laboratoryService;

    @MockitoBean
    private LaboratoryLinkAssembler laboratoryLinkAssembler;

    @Test
    void createOrder_shouldReturn201() throws Exception {
        LabOrderRequestDTO request = new LabOrderRequestDTO();
        request.setPatientId(1L);
        request.setDoctorId(2L);
        request.setExamType("Hemograma");

        LabOrderResponseDTO response = LabOrderResponseDTO.builder()
                .id(1L)
                .patientId(1L)
                .patientName("Juan Pérez")
                .doctorId(2L)
                .examType("Hemograma")
                .status("PENDIENTE")
                .build();

        when(laboratoryService.createOrder(any(LabOrderRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/lab/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.examType").value("Hemograma"))
                .andExpect(jsonPath("$.status").value("PENDIENTE"));
    }

    @Test
    void createOrder_shouldReturn400_whenInvalidData() throws Exception {
        LabOrderRequestDTO request = new LabOrderRequestDTO();

        mockMvc.perform(post("/api/lab/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadResult_shouldReturn200() throws Exception {
        ResultRequestDTO request = new ResultRequestDTO();
        request.setFindings("Hemoglobina baja");
        request.setDocumentUrl("http://docs.com/result.pdf");

        mockMvc.perform(put("/api/lab/results/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Result uploaded successfully"));
    }

    @Test
    void getPatientOrders_shouldReturn200() throws Exception {
        LabOrderResponseDTO order = LabOrderResponseDTO.builder()
                .id(1L)
                .patientId(1L)
                .patientName("Juan Pérez")
                .doctorId(2L)
                .examType("Hemograma")
                .status("PENDIENTE")
                .build();

        List<LabOrderResponseDTO> orders = List.of(order);
        EntityModel<LabOrderResponseDTO> model = EntityModel.of(order);

        when(laboratoryService.getPatientOrders(1L)).thenReturn(orders);
        when(laboratoryLinkAssembler.toModel(order)).thenReturn(model);

        mockMvc.perform(get("/api/lab/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.labOrderResponseDTOList[0].id").value(1L))
                .andExpect(jsonPath("$._embedded.labOrderResponseDTOList[0].examType").value("Hemograma"));
    }
}
