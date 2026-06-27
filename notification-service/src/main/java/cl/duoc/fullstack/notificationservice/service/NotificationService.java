package cl.duoc.fullstack.notificationservice.service;

import cl.duoc.fullstack.notificationservice.dto.NotificationRequestDTO;
import cl.duoc.fullstack.notificationservice.dto.NotificationResponseDTO;
import cl.duoc.fullstack.notificationservice.exception.DuplicateNotificationException;
import cl.duoc.fullstack.notificationservice.exception.NotificationNotFoundException;
import cl.duoc.fullstack.notificationservice.model.Notification;
import cl.duoc.fullstack.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationResponseDTO sendNotification(NotificationRequestDTO dto) {

        if (notificationRepository.findByRecipientIdAndTypeAndMessage(
                dto.getRecipientId(), dto.getType(), dto.getMessage()).isPresent()) {
            throw new DuplicateNotificationException("A notification with the same content already exists for recipient " + dto.getRecipientId());
        }

        Notification notification = Notification.builder()
                .recipientId(dto.getRecipientId())
                .type(dto.getType())
                .recipient(dto.getRecipient())
                .message(dto.getMessage())
                .sentAt(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notificación enviada: {}", savedNotification.getId());

        return mapToResponse(savedNotification);
    }

    public NotificationResponseDTO getById(Long id) {
        log.info("Recuperar notificación: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Notificación no encontrada: {}", id);
                    return new NotificationNotFoundException("Notification not found with id: " + id);
                });

        return mapToResponse(notification);
    }

    public List<NotificationResponseDTO> getNotificationHistory(Long recipientId) {

        return notificationRepository.findByRecipientId(recipientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private NotificationResponseDTO mapToResponse(Notification notification) {

        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .recipientId(notification.getRecipientId())
                .type(notification.getType())
                .recipient(notification.getRecipient())
                .message(notification.getMessage())
                .sentAt(notification.getSentAt())
                .build();
    }
}
