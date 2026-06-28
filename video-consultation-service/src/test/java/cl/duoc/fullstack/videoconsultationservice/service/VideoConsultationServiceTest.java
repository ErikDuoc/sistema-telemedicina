package cl.duoc.fullstack.videoconsultationservice.service;

import cl.duoc.fullstack.videoconsultationservice.client.AppointmentClient;
import cl.duoc.fullstack.videoconsultationservice.dto.VideoConsultationRequestDTO;
import cl.duoc.fullstack.videoconsultationservice.dto.VideoConsultationResponseDTO;
import cl.duoc.fullstack.videoconsultationservice.exception.DuplicateVideoConsultationException;
import cl.duoc.fullstack.videoconsultationservice.exception.VideoConsultationNotFoundException;
import cl.duoc.fullstack.videoconsultationservice.model.VideoConsultation;
import cl.duoc.fullstack.videoconsultationservice.repository.VideoConsultationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoConsultationServiceTest {

    @Mock
    private VideoConsultationRepository repository;

    @Mock
    private AppointmentClient appointmentClient;

    @InjectMocks
    private VideoConsultationService service;

    @Captor
    private ArgumentCaptor<VideoConsultation> captor;

    private VideoConsultationRequestDTO buildRequest(Long appointmentId, String platform) {
        VideoConsultationRequestDTO dto = new VideoConsultationRequestDTO();
        dto.setAppointmentId(appointmentId);
        dto.setPlatform(platform);
        return dto;
    }

    private VideoConsultation buildConsultation(Long id) {
        return VideoConsultation.builder()
                .id(id)
                .appointmentId(1L)
                .meetingUrl("https://telemedicina.cl/meeting/test-uuid")
                .platform("Zoom")
                .status("SCHEDULED")
                .createdAt("2026-06-26T23:00:00")
                .build();
    }

    @Test
    void create_shouldReturnVideoConsultation_whenDataIsValid() {
        VideoConsultationRequestDTO request = buildRequest(1L, "Zoom");
        VideoConsultation saved = buildConsultation(1L);

        when(repository.existsByAppointmentId(1L)).thenReturn(false);
        when(appointmentClient.validateAppointment(1L)).thenReturn(new Object());
        when(repository.save(any(VideoConsultation.class))).thenReturn(saved);

        VideoConsultationResponseDTO result = service.create(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getAppointmentId());
        assertEquals("Zoom", result.getPlatform());
        assertEquals("SCHEDULED", result.getStatus());
        verify(repository).save(any(VideoConsultation.class));
    }

    @Test
    void create_shouldThrowException_whenDuplicateAppointment() {
        VideoConsultationRequestDTO request = buildRequest(1L, "Zoom");

        when(repository.existsByAppointmentId(1L)).thenReturn(true);

        assertThrows(DuplicateVideoConsultationException.class, () -> service.create(request));

        verify(appointmentClient, never()).validateAppointment(any());
        verify(repository, never()).save(any());
    }

    @Test
    void create_shouldThrowException_whenAppointmentNotFound() {
        VideoConsultationRequestDTO request = buildRequest(99L, "Zoom");

        when(repository.existsByAppointmentId(99L)).thenReturn(false);
        when(appointmentClient.validateAppointment(99L)).thenThrow(new RuntimeException("Cita no encontrada"));

        assertThrows(RuntimeException.class, () -> service.create(request));

        verify(repository, never()).save(any());
    }

    @Test
    void create_shouldGenerateValidMeetingUrl() {
        VideoConsultationRequestDTO request = buildRequest(1L, "Zoom");
        VideoConsultation saved = buildConsultation(1L);

        when(repository.existsByAppointmentId(1L)).thenReturn(false);
        when(appointmentClient.validateAppointment(1L)).thenReturn(new Object());
        when(repository.save(captor.capture())).thenReturn(saved);

        service.create(request);

        VideoConsultation captured = captor.getValue();
        assertTrue(captured.getMeetingUrl().startsWith("https://telemedicina.cl/meeting/"));
        assertNotNull(captured.getMeetingUrl());
    }

    @Test
    void getById_shouldReturnVideoConsultation_whenExists() {
        VideoConsultation consultation = buildConsultation(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(consultation));

        VideoConsultationResponseDTO result = service.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getAppointmentId());
        assertEquals("Zoom", result.getPlatform());
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(VideoConsultationNotFoundException.class, () -> service.getById(99L));
    }
}
