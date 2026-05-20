package cl.duoc.fullstack.paymentservice.config;

import cl.duoc.fullstack.paymentservice.model.Insurance;
import cl.duoc.fullstack.paymentservice.model.PaymentMethod;
import cl.duoc.fullstack.paymentservice.model.PaymentStatus;
import cl.duoc.fullstack.paymentservice.model.Transaction;
import cl.duoc.fullstack.paymentservice.repository.InsuranceRepository;
import cl.duoc.fullstack.paymentservice.repository.TransactionRepository;
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

    private final InsuranceRepository insuranceRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public void run(String... args) {

        if (insuranceRepository.count() == 0) {
            log.info("Inicializando seguros...");

            List<Insurance> insurances = List.of(
                    Insurance.builder()
                            .name("Fonasa")
                            .coveragePercentage(80.0)
                            .build(),
                    Insurance.builder()
                            .name("Consalud")
                            .coveragePercentage(70.0)
                            .build(),
                    Insurance.builder()
                            .name("Banmedica")
                            .coveragePercentage(65.0)
                            .build(),
                    Insurance.builder()
                            .name("Isapre Vida")
                            .coveragePercentage(85.0)
                            .build()
            );

            insuranceRepository.saveAll(insurances);
            log.info("✅ {} seguros creados", insuranceRepository.count());
        }

        if (transactionRepository.count() == 0) {
            log.info("Inicializando transacciones de pago...");

            List<Transaction> transactions = List.of(
                    Transaction.builder()
                            .appointmentId(1L)
                            .amount(50000.0)
                            .paymentMethod(PaymentMethod.CREDIT_CARD)
                            .status(PaymentStatus.PENDING)
                            .createdAt(LocalDateTime.now().minusDays(2))
                            .build(),
                    Transaction.builder()
                            .appointmentId(2L)
                            .amount(45000.0)
                            .paymentMethod(PaymentMethod.TRANSFER)
                            .status(PaymentStatus.APPROVED)
                            .createdAt(LocalDateTime.now().minusDays(1))
                            .build(),
                    Transaction.builder()
                            .appointmentId(3L)
                            .amount(60000.0)
                            .paymentMethod(PaymentMethod.CASH)
                            .status(PaymentStatus.APPROVED)
                            .createdAt(LocalDateTime.now())
                            .build(),
                    Transaction.builder()
                            .appointmentId(4L)
                            .amount(50000.0)
                            .paymentMethod(PaymentMethod.CREDIT_CARD)
                            .status(PaymentStatus.APPROVED)
                            .createdAt(LocalDateTime.now().minusHours(5))
                            .build(),
                    Transaction.builder()
                            .appointmentId(5L)
                            .amount(55000.0)
                            .paymentMethod(PaymentMethod.TRANSFER)
                            .status(PaymentStatus.REJECTED)
                            .createdAt(LocalDateTime.now().minusHours(3))
                            .build()
            );

            transactionRepository.saveAll(transactions);
            log.info("✅ {} transacciones creadas", transactionRepository.count());
        }
    }
}
