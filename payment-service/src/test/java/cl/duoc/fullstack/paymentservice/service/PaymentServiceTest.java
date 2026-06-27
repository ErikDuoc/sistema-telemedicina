package cl.duoc.fullstack.paymentservice.service;

import cl.duoc.fullstack.paymentservice.dto.InsuranceDTO;
import cl.duoc.fullstack.paymentservice.dto.PaymentRequestDTO;
import cl.duoc.fullstack.paymentservice.dto.PaymentResponseDTO;
import cl.duoc.fullstack.paymentservice.exception.DuplicatePaymentException;
import cl.duoc.fullstack.paymentservice.exception.PaymentNotFoundException;
import cl.duoc.fullstack.paymentservice.model.Insurance;
import cl.duoc.fullstack.paymentservice.model.PaymentMethod;
import cl.duoc.fullstack.paymentservice.model.PaymentStatus;
import cl.duoc.fullstack.paymentservice.model.Transaction;
import cl.duoc.fullstack.paymentservice.repository.InsuranceRepository;
import cl.duoc.fullstack.paymentservice.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private InsuranceRepository insuranceRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Transaction buildTransaction(Long id, Long appointmentId, Double amount,
                                         PaymentMethod method, PaymentStatus status) {
        return Transaction.builder()
                .id(id)
                .appointmentId(appointmentId)
                .amount(amount)
                .paymentMethod(method)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private PaymentRequestDTO buildRequest(Long appointmentId, Double amount, PaymentMethod method) {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setAppointmentId(appointmentId);
        dto.setAmount(amount);
        dto.setPaymentMethod(method);
        return dto;
    }

    @Test
    void getAllInsurances_shouldReturnInsuranceList() {
        Insurance insurance = Insurance.builder()
                .id(1L)
                .name("Fonasa")
                .coveragePercentage(80.0)
                .build();
        when(insuranceRepository.findAll()).thenReturn(List.of(insurance));

        List<InsuranceDTO> result = paymentService.getAllInsurances();

        assertEquals(1, result.size());
        assertEquals("Fonasa", result.get(0).getName());
        assertEquals(80.0, result.get(0).getCoveragePercentage());
    }

    @Test
    void getAllPayments_shouldReturnPaymentList() {
        Transaction transaction = buildTransaction(1L, 1L, 50000.0,
                PaymentMethod.CREDIT_CARD, PaymentStatus.APPROVED);
        when(transactionRepository.findAll()).thenReturn(List.of(transaction));

        List<PaymentResponseDTO> result = paymentService.getAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(50000.0, result.get(0).getAmount());
        assertEquals(PaymentMethod.CREDIT_CARD, result.get(0).getPaymentMethod());
        assertEquals(PaymentStatus.APPROVED, result.get(0).getStatus());
    }

    @Test
    void getById_shouldReturnPayment_whenExists() {
        Transaction transaction = buildTransaction(1L, 1L, 50000.0,
                PaymentMethod.CREDIT_CARD, PaymentStatus.APPROVED);
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        PaymentResponseDTO result = paymentService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getAppointmentId());
        assertEquals(50000.0, result.getAmount());
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.getById(99L));
    }

    @Test
    void processPayment_shouldReturnPayment_whenDataIsValid() {
        PaymentRequestDTO request = buildRequest(1L, 50000.0, PaymentMethod.CREDIT_CARD);
        Transaction savedTransaction = buildTransaction(1L, 1L, 50000.0,
                PaymentMethod.CREDIT_CARD, PaymentStatus.APPROVED);

        when(transactionRepository.findAll()).thenReturn(List.of());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        PaymentResponseDTO result = paymentService.processPayment(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getAppointmentId());
        assertEquals(50000.0, result.getAmount());
        assertEquals(PaymentMethod.CREDIT_CARD, result.getPaymentMethod());
        assertEquals(PaymentStatus.APPROVED, result.getStatus());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void processPayment_shouldThrowException_whenDuplicate() {
        PaymentRequestDTO request = buildRequest(1L, 50000.0, PaymentMethod.CREDIT_CARD);
        Transaction existing = buildTransaction(2L, 1L, 50000.0,
                PaymentMethod.TRANSFER, PaymentStatus.APPROVED);

        when(transactionRepository.findAll()).thenReturn(List.of(existing));

        assertThrows(DuplicatePaymentException.class, () -> paymentService.processPayment(request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void processPayment_shouldThrowException_whenInvalidAmount() {
        PaymentRequestDTO request = buildRequest(1L, 0.0, PaymentMethod.CREDIT_CARD);

        assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment(request));

        verify(transactionRepository, never()).save(any());
    }
}
