package cl.duoc.fullstack.prescriptionservice.client;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ClinicalRecordClientFallback implements ClinicalRecordClient {

    private static final Logger logger = LoggerFactory.getLogger(ClinicalRecordClientFallback.class);

    @Override
    public Object getClinicalRecord(Long id) {
        logger.warn("ClinicalRecordClient fallback activado para ID: {}", id);
        return null;
    }
}
