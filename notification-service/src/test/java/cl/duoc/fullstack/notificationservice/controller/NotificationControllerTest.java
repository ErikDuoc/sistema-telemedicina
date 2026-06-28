package cl.duoc.fullstack.notificationservice.controller;

import cl.duoc.fullstack.notificationservice.config.SecurityConfig;
import cl.duoc.fullstack.notificationservice.dto.NotificationRequestDTO;
import cl.duoc.fullstack.notificationservice.dto.NotificationResponseDTO;
import cl.duoc.fullstack.notificationservice.exception.DuplicateNotificationException;
import cl.duoc.fullstack.notificationservice.exception.NotificationNotFoundException;
import cl.duoc.fullstack.notificationservice.model.NotificationType;
import cl.duoc.fullstack.notificationservice.service.NotificationLinkAssembler;
import cl.duoc.fullstack.notificationservice.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@Import(SecurityConfig.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NotificationService service;

    @MockitoBean
    private NotificationLinkAssembler assembler;

    @Test
    void sendNotification_shouldReturn201() throws Exception {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setRecipientId(1L);
        request.setType(NotificationType.EMAIL);
        request.setRecipient("test@example.com");
        request.setMessage("Su cita fue confirmada");

        NotificationResponseDTO response = NotificationResponseDTO.builder()
                .id(1L)
                .recipientId(1L)
                .type(NotificationType.EMAIL)
                .recipient("test@example.com")
                .message("Su cita fue confirmada")
                .sentAt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .build();

        when(service.sendNotification(any(NotificationRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.recipient").value("test@example.com"))
                .andExpect(jsonPath("$.message").value("Su cita fue confirmada"));
    }

    @Test
    void sendNotification_shouldReturn400_whenInvalidData() throws Exception {
        NotificationRequestDTO request = new NotificationRequestDTO();

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_shouldReturn200() throws Exception {
        NotificationResponseDTO response = NotificationResponseDTO.builder()
                .id(1L)
                .recipientId(1L)
                .type(NotificationType.EMAIL)
                .recipient("test@example.com")
                .message("Su cita fue confirmada")
                .sentAt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .build();

        when(service.getById(1L)).thenReturn(response);
        when(assembler.toModel(response)).thenReturn(EntityModel.of(response));

        mockMvc.perform(get("/api/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.recipient").value("test@example.com"));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        when(service.getById(99L)).thenThrow(new NotificationNotFoundException("Notification not found with id: 99"));

        mockMvc.perform(get("/api/notifications/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Notification not found with id: 99"));
    }

    @Test
    void getNotificationHistory_shouldReturn200() throws Exception {
        NotificationResponseDTO response = NotificationResponseDTO.builder()
                .id(1L)
                .recipientId(1L)
                .type(NotificationType.EMAIL)
                .recipient("test@example.com")
                .message("Su cita fue confirmada")
                .sentAt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .build();

        when(service.getNotificationHistory(1L)).thenReturn(List.of(response));
        when(assembler.toModel(response)).thenReturn(EntityModel.of(response));

        mockMvc.perform(get("/api/notifications/history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.notificationResponseDTOList[0].id").value(1L));
    }

    @Test
    void sendNotification_shouldReturn409_whenDuplicate() throws Exception {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setRecipientId(1L);
        request.setType(NotificationType.EMAIL);
        request.setRecipient("test@example.com");
        request.setMessage("Su cita fue confirmada");

        when(service.sendNotification(any(NotificationRequestDTO.class)))
                .thenThrow(new DuplicateNotificationException("Ya existe una notificación para este destinatario"));

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ya existe una notificación para este destinatario"));
    }
}
