package cl.duoc.fullstack.agendaservice.controller;

import cl.duoc.fullstack.agendaservice.config.SecurityConfig;
import cl.duoc.fullstack.agendaservice.dto.AvailabilityRequestDTO;
import cl.duoc.fullstack.agendaservice.dto.AvailabilityResponseDTO;
import cl.duoc.fullstack.agendaservice.exception.AgendaNotFoundException;
import cl.duoc.fullstack.agendaservice.exception.DuplicateAgendaException;
import cl.duoc.fullstack.agendaservice.service.AgendaService;
import cl.duoc.fullstack.agendaservice.service.AvailabilityLinkAssembler;
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

@WebMvcTest(AgendaController.class)
@Import(SecurityConfig.class)
class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AgendaService agendaService;

    @MockBean
    private AvailabilityLinkAssembler availabilityLinkAssembler;

    @Test
    void create_shouldReturn201() throws Exception {
        AvailabilityRequestDTO request = new AvailabilityRequestDTO();
        request.setDoctorId(1L);
        request.setDayOfWeek("MONDAY");
        request.setStartTime("09:00");
        request.setEndTime("17:00");

        AvailabilityResponseDTO response = AvailabilityResponseDTO.builder()
                .id(1L)
                .doctorId(1L)
                .doctorName("Juan Pérez")
                .dayOfWeek("MONDAY")
                .startTime("09:00")
                .endTime("17:00")
                .build();

        when(agendaService.create(any(AvailabilityRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/agenda/setup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorId").value(1))
                .andExpect(jsonPath("$.doctorName").value("Juan Pérez"))
                .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"));
    }

    @Test
    void getByDoctor_shouldReturn200() throws Exception {
        AvailabilityResponseDTO dto = AvailabilityResponseDTO.builder()
                .id(1L)
                .doctorId(1L)
                .doctorName("Juan Pérez")
                .dayOfWeek("MONDAY")
                .startTime("09:00")
                .endTime("17:00")
                .build();

        EntityModel<AvailabilityResponseDTO> entityModel = EntityModel.of(dto,
                linkTo(methodOn(AgendaController.class).getByDoctor(1L)).withSelfRel());

        when(agendaService.getByDoctor(1L)).thenReturn(List.of(dto));
        when(availabilityLinkAssembler.toModel(dto)).thenReturn(entityModel);

        mockMvc.perform(get("/api/agenda/doctor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.availabilityResponseDTOList[0].doctorName").value("Juan Pérez"))
                .andExpect(jsonPath("$._embedded.availabilityResponseDTOList[0]._links.self.href").value(linkTo(methodOn(AgendaController.class).getByDoctor(1L)).toString()))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void getByDoctor_shouldReturn404_whenNotFound() throws Exception {
        when(agendaService.getByDoctor(99L)).thenThrow(new AgendaNotFoundException("No availabilities found for doctor with id: 99"));

        mockMvc.perform(get("/api/agenda/doctor/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No availabilities found for doctor with id: 99"));
    }

    @Test
    void create_shouldReturn409_whenDuplicate() throws Exception {
        AvailabilityRequestDTO request = new AvailabilityRequestDTO();
        request.setDoctorId(1L);
        request.setDayOfWeek("MONDAY");
        request.setStartTime("09:00");
        request.setEndTime("17:00");

        when(agendaService.create(any(AvailabilityRequestDTO.class)))
                .thenThrow(new DuplicateAgendaException("Availability already exists for doctor 1 on MONDAY"));

        mockMvc.perform(post("/api/agenda/setup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Availability already exists for doctor 1 on MONDAY"));
    }

    @Test
    void create_shouldReturn400_whenInvalidData() throws Exception {
        AvailabilityRequestDTO request = new AvailabilityRequestDTO();
        request.setDayOfWeek("MONDAY");

        mockMvc.perform(post("/api/agenda/setup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}
