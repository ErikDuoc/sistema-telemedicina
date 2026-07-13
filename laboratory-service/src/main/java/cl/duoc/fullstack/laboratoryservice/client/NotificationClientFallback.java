package cl.duoc.fullstack.laboratoryservice.client;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class NotificationClientFallback implements NotificationClient {

    private static final Logger logger = LoggerFactory.getLogger(NotificationClientFallback.class);

    @Override
    public Object sendNotification(Long id) {
        logger.warn("NotificationClient fallback activado para ID: {}", id);
        return null;
    }
}
