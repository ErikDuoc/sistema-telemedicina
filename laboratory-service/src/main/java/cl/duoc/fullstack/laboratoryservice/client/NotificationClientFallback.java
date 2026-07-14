package cl.duoc.fullstack.laboratoryservice.client;

import cl.duoc.fullstack.laboratoryservice.dto.NotificationRequestDTO;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class NotificationClientFallback implements NotificationClient {

    private static final Logger logger = LoggerFactory.getLogger(NotificationClientFallback.class);

    @Override
    public void sendNotification(NotificationRequestDTO dto) {
        logger.warn("NotificationClient fallback activado para notificacion: {}", dto);
    }
}
