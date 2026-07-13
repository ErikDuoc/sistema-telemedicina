package cl.duoc.fullstack.appointmentservice.client;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DoctorClientFallback implements DoctorClient {

    private static final Logger logger = LoggerFactory.getLogger(DoctorClientFallback.class);

    @Override
    public Object getDoctor(Long id) {
        logger.warn("DoctorClient fallback activado para ID: {}", id);
        return null;
    }
}
