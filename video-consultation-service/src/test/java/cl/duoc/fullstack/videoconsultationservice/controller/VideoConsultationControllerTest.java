package cl.duoc.fullstack.videoconsultationservice.controller;

import cl.duoc.fullstack.videoconsultationservice.config.SecurityConfig;
import cl.duoc.fullstack.videoconsultationservice.dto.VideoConsultationRequestDTO;
import cl.duoc.fullstack.videoconsultationservice.dto.VideoConsultationResponseDTO;
import cl.duoc.fullstack.videoconsultationservice.exception.DuplicateVideoConsultationException;
import cl.duoc.fullstack.videoconsultationservice.exception.VideoConsultationNotFoundException;
import cl.duoc.fullstack.videoconsultationservice.service.VideoConsultationLinkAssembler;
import cl.duoc.fullstack.videoconsultationservice.service.VideoConsultationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideoConsultationController.class)
@Import(SecurityConfig.class)
class VideoConsultationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VideoConsultationService videoConsultationService;

    @MockitoBean
    private VideoConsultationLinkAssembler videoConsultationLinkAssembler;

    @Test
    void create_shouldReturn201() throws Exception {
        VideoConsultationRequestDTO request = new VideoConsultationRequestDTO();
        request.setAppointmentId(1L);
        request.setPlatform("Zoom");

        VideoConsultationResponseDTO response = VideoConsultationResponseDTO.builder()
                .id(1L)
                .appointmentId(1L)
                .meetingUrl("https://telemedicina.cl/meeting/test-uuid")
                .platform("Zoom")
                .status("SCHEDULED")
                .createdAt("2026-06-26T23:00:00")
                .build();

        when(videoConsultationService.create(any(VideoConsultationRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/video-consultations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.platform").value("Zoom"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void create_shouldReturn400_whenInvalidData() throws Exception {
        mockMvc.perform(post("/api/video-consultations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void getById_shouldReturn200_whenExists() throws Exception {
        VideoConsultationResponseDTO dto = VideoConsultationResponseDTO.builder()
                .id(1L)
                .appointmentId(1L)
                .meetingUrl("https://telemedicina.cl/meeting/test-uuid")
                .platform("Zoom")
                .status("SCHEDULED")
                .createdAt("2026-06-26T23:00:00")
                .build();

        when(videoConsultationService.getById(1L)).thenReturn(dto);
        when(videoConsultationLinkAssembler.toModel(dto)).thenReturn(EntityModel.of(dto));

        mockMvc.perform(get("/api/video-consultations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.platform").value("Zoom"));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        when(videoConsultationService.getById(99L))
                .thenThrow(new VideoConsultationNotFoundException("Video consultation not found"));

        mockMvc.perform(get("/api/video-consultations/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void create_shouldReturn409_whenDuplicate() throws Exception {
        VideoConsultationRequestDTO request = new VideoConsultationRequestDTO();
        request.setAppointmentId(1L);
        request.setPlatform("Zoom");

        when(videoConsultationService.create(any(VideoConsultationRequestDTO.class)))
                .thenThrow(new DuplicateVideoConsultationException("Ya existe una videoconsulta para la cita: 1"));

        mockMvc.perform(post("/api/video-consultations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"));
    }
}
