package cl.duoc.fullstack.prescriptionservice.config;

import cl.duoc.fullstack.prescriptionservice.model.Prescription;
import cl.duoc.fullstack.prescriptionservice.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PrescriptionRepository prescriptionRepository;

    @Override
    public void run(String... args) {
        if (prescriptionRepository.count() == 0) {
            log.info("Inicializando recetas de ejemplo...");
            
            List<Prescription> prescriptions = List.of(
                Prescription.builder()
                        .clinicalRecordId(1L)
                        .patientId(1L)
                        .medication("Amoxicilina 500mg x 20 cápsulas\nIbuprofeno 400mg x 30 tabletas")
                        .indications("Tomar Amoxicilina cada 8 horas por 7 días\nIbuprofeno cada 6 horas según sea necesario para el dolor")
                        .createdAt(LocalDateTime.now().minusDays(3).toString())
                        .build(),
                Prescription.builder()
                        .clinicalRecordId(2L)
                        .patientId(2L)
                        .medication("Atorvastatina 20mg x 30 tabletas\nMetformina 500mg x 60 tabletas")
                        .indications("Atorvastatina 1 tableta cada noche\nMetformina 1 tableta con cada comida principal")
                        .createdAt(LocalDateTime.now().minusDays(5).toString())
                        .build(),
                Prescription.builder()
                        .clinicalRecordId(3L)
                        .patientId(3L)
                        .medication("Omeprazol 20mg x 30 cápsulas\nDomperidona 10mg x 30 tabletas")
                        .indications("Omeprazol 1 cápsula en ayunas\nDomperidona 1 tableta antes de las comidas")
                        .createdAt(LocalDateTime.now().minusDays(1).toString())
                        .build(),
                Prescription.builder()
                        .clinicalRecordId(4L)
                        .patientId(4L)
                        .medication("Paracetamol 500mg x 20 tabletas")
                        .indications("1-2 tabletas cada 4-6 horas, no exceder 4g diarios")
                        .createdAt(LocalDateTime.now().toString())
                        .build()
            );
            
            prescriptionRepository.saveAll(prescriptions);
            log.info("✅ {} recetas creadas", prescriptionRepository.count());
        }
    }
}

