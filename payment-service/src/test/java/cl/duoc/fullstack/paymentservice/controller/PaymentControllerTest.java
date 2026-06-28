package cl.duoc.fullstack.paymentservice.controller;

import cl.duoc.fullstack.paymentservice.config.SecurityConfig;
import cl.duoc.fullstack.paymentservice.dto.InsuranceDTO;
import cl.duoc.fullstack.paymentservice.dto.PaymentRequestDTO;
import cl.duoc.fullstack.paymentservice.dto.PaymentResponseDTO;
import cl.duoc.fullstack.paymentservice.exception.DuplicatePaymentException;
import cl.duoc.fullstack.paymentservice.exception.PaymentNotFoundException;
import cl.duoc.fullstack.paymentservice.model.PaymentMethod;
import cl.duoc.fullstack.paymentservice.model.PaymentStatus;
import cl.duoc.fullstack.paymentservice.service.InsuranceLinkAssembler;
import cl.duoc.fullstack.paymentservice.service.PaymentLinkAssembler;
import cl.duoc.fullstack.paymentservice.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@Import(SecurityConfig.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private PaymentLinkAssembler paymentLinkAssembler;

    @MockitoBean
    private InsuranceLinkAssembler insuranceLinkAssembler;

    @Test
    void getAllInsurances_shouldReturn200() throws Exception {
        InsuranceDTO insurance = InsuranceDTO.builder()
                .id(1L)
                .name("Fonasa")
                .coveragePercentage(80.0)
                .build();

        when(paymentService.getAllInsurances()).thenReturn(List.of(insurance));
        when(insuranceLinkAssembler.toModel(insurance)).thenReturn(EntityModel.of(insurance));

        mockMvc.perform(get("/api/payments/insurances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.insuranceDTOList[0].id").value(1L))
                .andExpect(jsonPath("$._embedded.insuranceDTOList[0].name").value("Fonasa"));
    }

    @Test
    void getAll_shouldReturn200() throws Exception {
        PaymentResponseDTO payment = PaymentResponseDTO.builder()
                .id(1L)
                .appointmentId(1L)
                .amount(50000.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentService.getAll()).thenReturn(List.of(payment));
        when(paymentLinkAssembler.toModel(payment)).thenReturn(EntityModel.of(payment));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.paymentResponseDTOList[0].id").value(1L))
                .andExpect(jsonPath("$._embedded.paymentResponseDTOList[0].amount").value(50000.0));
    }

    @Test
    void getById_shouldReturn200_whenExists() throws Exception {
        PaymentResponseDTO payment = PaymentResponseDTO.builder()
                .id(1L)
                .appointmentId(1L)
                .amount(50000.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentService.getById(1L)).thenReturn(payment);
        when(paymentLinkAssembler.toModel(payment)).thenReturn(EntityModel.of(payment));

        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.amount").value(50000.0));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {
        when(paymentService.getById(99L))
                .thenThrow(new PaymentNotFoundException("Transacción no encontrada: 99"));

        mockMvc.perform(get("/api/payments/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void processPayment_shouldReturn201() throws Exception {
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setAppointmentId(1L);
        request.setAmount(50000.0);
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        PaymentResponseDTO response = PaymentResponseDTO.builder()
                .id(1L)
                .appointmentId(1L)
                .amount(50000.0)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentService.processPayment(any(PaymentRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.amount").value(50000.0));
    }

    @Test
    void processPayment_shouldReturn400_whenInvalidData() throws Exception {
        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void processPayment_shouldReturn409_whenDuplicate() throws Exception {
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setAppointmentId(1L);
        request.setAmount(50000.0);
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        when(paymentService.processPayment(any(PaymentRequestDTO.class)))
                .thenThrow(new DuplicatePaymentException("Ya existe un pago para la cita: 1"));

        mockMvc.perform(post("/api/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"));
    }
}
