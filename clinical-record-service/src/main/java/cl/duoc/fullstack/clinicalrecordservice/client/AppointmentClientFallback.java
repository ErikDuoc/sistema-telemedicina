package cl.duoc.fullstack.clinicalrecordservice.client;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AppointmentClientFallback implements AppointmentClient {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentClientFallback.class);

    @Override
    public Object validateAppointment(Long id) {
        logger.warn("AppointmentClient fallback activado para ID: {}", id);
        return null;
    }
}
