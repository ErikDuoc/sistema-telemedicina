package cl.duoc.fullstack.laboratoryservice.client;

import cl.duoc.fullstack.laboratoryservice.dto.PatientResponseDTO;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PatientClientFallback implements PatientClient {

    private static final Logger logger = LoggerFactory.getLogger(PatientClientFallback.class);

    @Override
    public PatientResponseDTO getPatient(Long id) {
        logger.warn("PatientClient fallback activado para ID: {}", id);
        return null;
    }
}
