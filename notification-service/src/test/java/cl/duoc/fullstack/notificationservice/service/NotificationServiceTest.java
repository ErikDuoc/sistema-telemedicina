package cl.duoc.fullstack.notificationservice.service;

import cl.duoc.fullstack.notificationservice.dto.NotificationRequestDTO;
import cl.duoc.fullstack.notificationservice.dto.NotificationResponseDTO;
import cl.duoc.fullstack.notificationservice.exception.DuplicateNotificationException;
import cl.duoc.fullstack.notificationservice.exception.NotificationNotFoundException;
import cl.duoc.fullstack.notificationservice.model.Notification;
import cl.duoc.fullstack.notificationservice.model.NotificationType;
import cl.duoc.fullstack.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationService service;

    private NotificationRequestDTO buildValidRequest() {
        NotificationRequestDTO dto = new NotificationRequestDTO();
        dto.setRecipientId(1L);
        dto.setType(NotificationType.EMAIL);
        dto.setRecipient("test@example.com");
        dto.setMessage("Su cita fue confirmada");
        return dto;
    }

    @Test
    void sendNotification_shouldReturnResponse_whenDataIsValid() {
        NotificationRequestDTO request = buildValidRequest();

        Notification savedNotification = Notification.builder()
                .id(1L)
                .recipientId(1L)
                .type(NotificationType.EMAIL)
                .recipient("test@example.com")
                .message("Su cita fue confirmada")
                .sentAt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .build();

        when(repository.findByRecipientIdAndTypeAndMessage(1L, NotificationType.EMAIL, "Su cita fue confirmada"))
                .thenReturn(Optional.empty());
        when(repository.save(any(Notification.class))).thenReturn(savedNotification);

        NotificationResponseDTO result = service.sendNotification(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getRecipientId());
        assertEquals(NotificationType.EMAIL, result.getType());
        assertEquals("test@example.com", result.getRecipient());
        assertEquals("Su cita fue confirmada", result.getMessage());

        verify(repository).findByRecipientIdAndTypeAndMessage(1L, NotificationType.EMAIL, "Su cita fue confirmada");
        verify(repository).save(any(Notification.class));
    }

    @Test
    void sendNotification_shouldThrowException_whenDuplicateFound() {
        NotificationRequestDTO request = buildValidRequest();

        Notification existing = Notification.builder().id(99L).build();

        when(repository.findByRecipientIdAndTypeAndMessage(1L, NotificationType.EMAIL, "Su cita fue confirmada"))
                .thenReturn(Optional.of(existing));

        assertThrows(DuplicateNotificationException.class, () -> service.sendNotification(request));

        verify(repository, never()).save(any());
    }

    @Test
    void getById_shouldReturnNotification_whenExists() {
        Notification notification = Notification.builder()
                .id(1L)
                .recipientId(1L)
                .type(NotificationType.EMAIL)
                .recipient("test@example.com")
                .message("Su cita fue confirmada")
                .sentAt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(notification));

        NotificationResponseDTO result = service.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getRecipient());
    }

    @Test
    void getById_shouldThrowNotFoundException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    void getNotificationHistory_shouldReturnList_whenNotificationsExist() {
        List<Notification> notifications = List.of(
                Notification.builder().id(1L).recipientId(1L).type(NotificationType.EMAIL).recipient("test@example.com").message("Mensaje 1").sentAt(LocalDateTime.now()).build(),
                Notification.builder().id(2L).recipientId(1L).type(NotificationType.SMS).recipient("+56912345678").message("Mensaje 2").sentAt(LocalDateTime.now()).build()
        );

        when(repository.findByRecipientId(1L)).thenReturn(notifications);

        List<NotificationResponseDTO> result = service.getNotificationHistory(1L);

        assertEquals(2, result.size());
        assertEquals("Mensaje 1", result.get(0).getMessage());
        assertEquals("Mensaje 2", result.get(1).getMessage());
    }

    @Test
    void getNotificationHistory_shouldReturnEmptyList_whenNoNotifications() {
        when(repository.findByRecipientId(99L)).thenReturn(List.of());

        List<NotificationResponseDTO> result = service.getNotificationHistory(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void sendNotification_shouldSetSentAtTimestamp() {
        NotificationRequestDTO request = buildValidRequest();

        when(repository.findByRecipientIdAndTypeAndMessage(1L, NotificationType.EMAIL, "Su cita fue confirmada"))
                .thenReturn(Optional.empty());
        when(repository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification rec = invocation.getArgument(0);
            assertNotNull(rec.getSentAt());
            return Notification.builder().id(1L).recipientId(1L).type(NotificationType.EMAIL)
                    .recipient("test@example.com").message("Su cita fue confirmada")
                    .sentAt(rec.getSentAt()).build();
        });

        service.sendNotification(request);
    }
}
