package cl.duoc.fullstack.notificationservice.repository;

import cl.duoc.fullstack.notificationservice.model.Notification;
import cl.duoc.fullstack.notificationservice.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientId(Long recipientId);

    Optional<Notification> findByRecipientIdAndTypeAndMessage(Long recipientId, NotificationType type, String message);
}
