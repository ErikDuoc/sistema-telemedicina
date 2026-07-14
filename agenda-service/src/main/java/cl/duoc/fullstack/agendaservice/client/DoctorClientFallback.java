package cl.duoc.fullstack.agendaservice.client;

import cl.duoc.fullstack.agendaservice.dto.DoctorResponseDTO;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DoctorClientFallback implements DoctorClient {

    private static final Logger logger = LoggerFactory.getLogger(DoctorClientFallback.class);

    @Override
    public DoctorResponseDTO getDoctor(Long id) {
        logger.warn("DoctorClient fallback activado para ID: {}", id);
        return null;
    }
}
