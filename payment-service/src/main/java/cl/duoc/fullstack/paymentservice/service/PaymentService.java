package cl.duoc.fullstack.paymentservice.service;

import cl.duoc.fullstack.paymentservice.dto.InsuranceDTO;
import cl.duoc.fullstack.paymentservice.dto.PaymentRequestDTO;
import cl.duoc.fullstack.paymentservice.dto.PaymentResponseDTO;
import cl.duoc.fullstack.paymentservice.exception.DuplicatePaymentException;
import cl.duoc.fullstack.paymentservice.exception.PaymentNotFoundException;
import cl.duoc.fullstack.paymentservice.model.*;
import cl.duoc.fullstack.paymentservice.repository.InsuranceRepository;
import cl.duoc.fullstack.paymentservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final InsuranceRepository insuranceRepository;

    public PaymentResponseDTO processPayment(PaymentRequestDTO dto) {
        log.info("Procesar pago para cita: {}, Monto: ${}", dto.getAppointmentId(), dto.getAmount());

        // Validación 1: Monto debe ser positivo
        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            log.warn("Monto inválido: {}", dto.getAmount());
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }

        // Validación 2: Cita debe existir (en una aplicación real, llamar a appointment-service)
        if (dto.getAppointmentId() == null || dto.getAppointmentId() <= 0) {
            log.warn("ID de cita inválido: {}", dto.getAppointmentId());
            throw new IllegalArgumentException("ID de cita inválido");
        }

        // Validación 3: Método de pago no debe ser nulo
        if (dto.getPaymentMethod() == null) {
            log.warn("Método de pago no especificado");
            throw new IllegalArgumentException("Debe especificar un método de pago");
        }

        // Validación 4: Evitar pagos duplicados (mismo appointmentId con estado APPROVED)
        boolean existingApprovedPayment = transactionRepository.findAll()
                .stream()
                .anyMatch(t -> t.getAppointmentId().equals(dto.getAppointmentId())
                        && t.getStatus().equals(PaymentStatus.APPROVED));

        if (existingApprovedPayment) {
            log.warn("Pago ya existe para cita: {}", dto.getAppointmentId());
                    throw new DuplicatePaymentException("Ya existe un pago aprobado para esta cita");
        }

        // Crear transacción
        Transaction transaction = Transaction.builder()
                .appointmentId(dto.getAppointmentId())
                .amount(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod())
                .status(PaymentStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Pago procesado exitosamente - Transacción ID: {}", savedTransaction.getId());

        return mapToResponse(savedTransaction);
    }

    public List<InsuranceDTO> getAllInsurances() {
        log.info("Recuperar todos los seguros");
        return insuranceRepository.findAll()
                .stream()
                .map(insurance -> InsuranceDTO.builder()
                        .id(insurance.getId())
                        .name(insurance.getName())
                        .coveragePercentage(insurance.getCoveragePercentage())
                        .build())
                .toList();
    }

    public PaymentResponseDTO getById(Long id) {
        log.info("Recuperar transacción por ID: {}", id);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Transacción no encontrada: {}", id);
                    return new PaymentNotFoundException("Transacción no encontrada: " + id);
                });
        return mapToResponse(transaction);
    }

    public List<PaymentResponseDTO> getAll() {
        log.info("Recuperar todas las transacciones");
        return transactionRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PaymentResponseDTO mapToResponse(Transaction transaction) {

        return PaymentResponseDTO.builder()
                .id(transaction.getId())
                .appointmentId(transaction.getAppointmentId())
                .amount(transaction.getAmount())
                .paymentMethod(transaction.getPaymentMethod())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
