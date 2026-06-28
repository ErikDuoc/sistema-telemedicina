package cl.duoc.fullstack.appointmentservice.controller;

import cl.duoc.fullstack.appointmentservice.config.SecurityConfig;
import cl.duoc.fullstack.appointmentservice.dto.AppointmentRequest;
import cl.duoc.fullstack.appointmentservice.dto.AppointmentResponseDTO;
import cl.duoc.fullstack.appointmentservice.exception.AppointmentNotFoundException;
import cl.duoc.fullstack.appointmentservice.model.Appointment;
import cl.duoc.fullstack.appointmentservice.service.AppointmentLinkAssembler;
import cl.duoc.fullstack.appointmentservice.service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
@Import(SecurityConfig.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppointmentService appointmentService;

    @MockBean
    private AppointmentLinkAssembler appointmentLinkAssembler;

    @Test
    void create_shouldReturn201() throws Exception {
        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(1L);
        request.setDoctorId(1L);
        request.setDate("2026-07-15");
        request.setTime("10:00");

        AppointmentResponseDTO response = AppointmentResponseDTO.builder()
                .id(1L)
                .patientId(1L)
                .doctorId(1L)
                .date("2026-07-15")
                .time("10:00")
                .status("PENDING")
                .build();

        when(appointmentService.create(any(AppointmentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.doctorId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getByPatient_shouldReturn200() throws Exception {
        Appointment appointment = Appointment.builder()
                .id(1L).patientId(1L).doctorId(1L).date("2026-07-15").time("10:00").status("PENDING").build();

        AppointmentResponseDTO dto = AppointmentResponseDTO.builder()
                .id(1L).patientId(1L).doctorId(1L).date("2026-07-15").time("10:00").status("PENDING").build();

        EntityModel<AppointmentResponseDTO> entityModel = EntityModel.of(dto,
                linkTo(methodOn(AppointmentController.class).getByPatient(1L)).withSelfRel());

        when(appointmentService.getByPatient(1L)).thenReturn(List.of(appointment));
        when(appointmentLinkAssembler.toModel(any(AppointmentResponseDTO.class))).thenReturn(entityModel);

        mockMvc.perform(get("/api/appointments/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.appointmentResponseDTOList[0].status").value("PENDING"))
                .andExpect(jsonPath("$._embedded.appointmentResponseDTOList[0]._links.self.href").value(linkTo(methodOn(AppointmentController.class).getByPatient(1L)).toString()))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void updateStatus_shouldReturn200() throws Exception {
        AppointmentResponseDTO response = AppointmentResponseDTO.builder()
                .id(1L).patientId(1L).doctorId(1L).date("2026-07-15").time("10:00").status("CONFIRMED").build();

        when(appointmentService.updateStatus(1L, "CONFIRMED")).thenReturn(response);

        mockMvc.perform(patch("/api/appointments/1/status")
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void updateStatus_shouldReturn404_whenNotFound() throws Exception {
        when(appointmentService.updateStatus(99L, "CONFIRMED"))
                .thenThrow(new AppointmentNotFoundException("Cita no encontrada"));

        mockMvc.perform(patch("/api/appointments/99/status")
                        .param("status", "CONFIRMED"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cita no encontrada"));
    }

    @Test
    void updateStatus_shouldReturn400_whenInvalidStatus() throws Exception {
        when(appointmentService.updateStatus(1L, "INVALID"))
                .thenThrow(new IllegalArgumentException("Estado de cita invalido"));

        mockMvc.perform(patch("/api/appointments/1/status")
                        .param("status", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Estado de cita invalido"));
    }

    @Test
    void create_shouldReturn400_whenInvalidData() throws Exception {
        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(1L);

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}
