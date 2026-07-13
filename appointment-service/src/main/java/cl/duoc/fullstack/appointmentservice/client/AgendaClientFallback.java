package cl.duoc.fullstack.appointmentservice.client;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AgendaClientFallback implements AgendaClient {

    private static final Logger logger = LoggerFactory.getLogger(AgendaClientFallback.class);

    @Override
    public Object getDoctorAgenda(Long id) {
        logger.warn("AgendaClient fallback activado para ID: {}", id);
        return null;
    }
}
