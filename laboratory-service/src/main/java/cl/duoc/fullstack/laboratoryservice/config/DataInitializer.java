package cl.duoc.fullstack.laboratoryservice.config;

import cl.duoc.fullstack.laboratoryservice.model.LabOrder;
import cl.duoc.fullstack.laboratoryservice.repository.LabOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final LabOrderRepository labOrderRepository;

    @Override
    public void run(String... args) {
        if (labOrderRepository.count() == 0) {
            log.info("Inicializando órdenes de laboratorio de ejemplo...");

            List<LabOrder> orders = List.of(
                LabOrder.builder()
                        .patientId(1L)
                        .orderType("HEMATOLOGIA")
                        .doctorId(1L)
                        .examType("Examen Completo de Sangre")
                        .status("PENDIENTE")
                        .build(),
                LabOrder.builder()
                        .patientId(2L)
                        .orderType("BIOQUIMICA")
                        .doctorId(2L)
                        .examType("Glucosa en Ayunas")
                        .status("COMPLETADO")
                        .build(),
                LabOrder.builder()
                        .patientId(3L)
                        .orderType("BIOQUIMICA")
                        .doctorId(1L)
                        .examType("Perfil Lipídico")
                        .status("PENDIENTE")
                        .build(),
                LabOrder.builder()
                        .patientId(4L)
                        .orderType("CARDIOLOGIA")
                        .doctorId(3L)
                        .examType("Electrocardiograma")
                        .status("COMPLETADO")
                        .build(),
                LabOrder.builder()
                        .patientId(5L)
                        .orderType("IMAGENOLOGIA")
                        .doctorId(2L)
                        .examType("Radiografía de Tórax")
                        .status("PENDIENTE")
                        .build()
            );

            labOrderRepository.saveAll(orders);
            log.info("✅ {} órdenes de laboratorio creadas", labOrderRepository.count());
        }
    }
}

