package cl.duoc.fullstack.notificationservice.service;

import cl.duoc.fullstack.notificationservice.dto.NotificationRequestDTO;
import cl.duoc.fullstack.notificationservice.dto.NotificationResponseDTO;
import cl.duoc.fullstack.notificationservice.model.Notification;
import cl.duoc.fullstack.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationResponseDTO sendNotification(NotificationRequestDTO dto) {

        Notification notification = Notification.builder()
                .recipientId(dto.getRecipientId())
                .type(dto.getType())
                .recipient(dto.getRecipient())
                .message(dto.getMessage())
                .sentAt(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        return mapToResponse(savedNotification);
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
