package cl.duoc.fullstack.doctorservice.controller;

import cl.duoc.fullstack.doctorservice.config.SecurityConfig;
import cl.duoc.fullstack.doctorservice.dto.DoctorRequestDTO;
import cl.duoc.fullstack.doctorservice.dto.DoctorResponseDTO;
import cl.duoc.fullstack.doctorservice.exception.DoctorNotFoundException;
import cl.duoc.fullstack.doctorservice.exception.DuplicateDoctorException;
import cl.duoc.fullstack.doctorservice.service.DoctorLinkAssembler;
import cl.duoc.fullstack.doctorservice.service.DoctorService;
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

@WebMvcTest(DoctorController.class)
@Import(SecurityConfig.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private DoctorLinkAssembler doctorLinkAssembler;

    @Test
    void createDoctor_shouldReturn201() throws Exception {
        DoctorRequestDTO request = new DoctorRequestDTO();
        request.setFirstName("Juan");
        request.setLastName("Pérez");
        request.setNationalRegistry("11.111.111-1");
        request.setEmail("juan@mail.com");
        request.setSpecialtyId(1L);

        DoctorResponseDTO response = DoctorResponseDTO.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .nationalRegistry("11.111.111-1")
                .email("juan@mail.com")
                .specialtyName("Cardiología")
                .build();

        when(doctorService.createDoctor(any(DoctorRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Juan"))
                .andExpect(jsonPath("$.specialtyName").value("Cardiología"));
    }

    @Test
    void getAllDoctors_shouldReturn200() throws Exception {
        DoctorResponseDTO dto = DoctorResponseDTO.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .nationalRegistry("11.111.111-1")
                .email("juan@mail.com")
                .specialtyName("Cardiología")
                .build();

        EntityModel<DoctorResponseDTO> entityModel = EntityModel.of(dto,
                linkTo(methodOn(DoctorController.class).getDoctorById(1L)).withSelfRel(),
                linkTo(methodOn(DoctorController.class).getAllDoctors()).withRel("all"));

        when(doctorService.getAllDoctors()).thenReturn(List.of(dto));
        when(doctorLinkAssembler.toModel(dto)).thenReturn(entityModel);

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.doctorResponseDTOList[0].firstName").value("Juan"))
                .andExpect(jsonPath("$._embedded.doctorResponseDTOList[0]._links.self.href").value(linkTo(methodOn(DoctorController.class).getDoctorById(1L)).toString()))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    void getDoctorById_shouldReturn200() throws Exception {
        DoctorResponseDTO dto = DoctorResponseDTO.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .nationalRegistry("11.111.111-1")
                .email("juan@mail.com")
                .specialtyName("Cardiología")
                .build();

        EntityModel<DoctorResponseDTO> entityModel = EntityModel.of(dto,
                linkTo(methodOn(DoctorController.class).getDoctorById(1L)).withSelfRel());

        when(doctorService.getDoctorById(1L)).thenReturn(dto);
        when(doctorLinkAssembler.toModel(dto)).thenReturn(entityModel);

        mockMvc.perform(get("/api/doctors/1/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Juan"))
                .andExpect(jsonPath("$._links.self.href").value(linkTo(methodOn(DoctorController.class).getDoctorById(1L)).toString()));
    }

    @Test
    void getDoctorById_shouldReturn404_whenNotFound() throws Exception {
        when(doctorService.getDoctorById(99L)).thenThrow(new DoctorNotFoundException("Doctor not found with id: 99"));

        mockMvc.perform(get("/api/doctors/99/profile"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor not found with id: 99"));
    }

    @Test
    void createDoctor_shouldReturn409_whenDuplicate() throws Exception {
        DoctorRequestDTO request = new DoctorRequestDTO();
        request.setFirstName("Juan");
        request.setLastName("Pérez");
        request.setNationalRegistry("11.111.111-1");
        request.setEmail("juan@mail.com");
        request.setSpecialtyId(1L);

        when(doctorService.createDoctor(any(DoctorRequestDTO.class)))
                .thenThrow(new DuplicateDoctorException("Doctor already exists with this national registry"));

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Doctor already exists with this national registry"));
    }
}
