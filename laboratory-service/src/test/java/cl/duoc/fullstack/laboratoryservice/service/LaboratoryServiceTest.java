package cl.duoc.fullstack.laboratoryservice.service;

import cl.duoc.fullstack.laboratoryservice.client.NotificationClient;
import cl.duoc.fullstack.laboratoryservice.client.PatientClient;
import cl.duoc.fullstack.laboratoryservice.dto.*;
import cl.duoc.fullstack.laboratoryservice.exception.DuplicateLaboratoryException;
import cl.duoc.fullstack.laboratoryservice.exception.LaboratoryNotFoundException;
import cl.duoc.fullstack.laboratoryservice.model.*;
import cl.duoc.fullstack.laboratoryservice.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LaboratoryServiceTest {

    @Mock
    private LabOrderRepository orderRepository;

    @Mock
    private LabResultRepository resultRepository;

    @Mock
    private PatientClient patientClient;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private LaboratoryService service;

    private LabOrderRequestDTO buildValidOrderRequest() {
        LabOrderRequestDTO dto = new LabOrderRequestDTO();
        dto.setPatientId(1L);
        dto.setDoctorId(2L);
        dto.setExamType("Hemograma");
        return dto;
    }

    private PatientResponseDTO buildPatientResponse() {
        PatientResponseDTO patient = new PatientResponseDTO();
        patient.setId(1L);
        patient.setFirstName("Juan");
        patient.setLastName("Pérez");
        patient.setEmail("juan@example.com");
        return patient;
    }

    @Test
    void createOrder_shouldReturnResponse_whenDataIsValid() {
        LabOrderRequestDTO request = buildValidOrderRequest();
        PatientResponseDTO patient = buildPatientResponse();

        LabOrder savedOrder = LabOrder.builder()
                .id(1L)
                .patientId(1L)
                .doctorId(2L)
                .examType("Hemograma")
                .status("PENDIENTE")
                .build();

        when(patientClient.getPatient(1L)).thenReturn(patient);
        when(orderRepository.save(any(LabOrder.class))).thenReturn(savedOrder);

        LabOrderResponseDTO result = service.createOrder(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getPatientId());
        assertEquals(2L, result.getDoctorId());
        assertEquals("Hemograma", result.getExamType());
        assertEquals("PENDIENTE", result.getStatus());

        verify(patientClient).getPatient(1L);
        verify(orderRepository).save(any(LabOrder.class));
    }

    @Test
    void createOrder_shouldThrowException_whenPatientNotFound() {
        LabOrderRequestDTO request = buildValidOrderRequest();

        doThrow(new RuntimeException("Paciente no encontrado")).when(patientClient).getPatient(1L);

        assertThrows(IllegalArgumentException.class, () -> service.createOrder(request));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_shouldThrowException_whenDoctorIdInvalid() {
        LabOrderRequestDTO request = buildValidOrderRequest();
        PatientResponseDTO patient = buildPatientResponse();
        request.setDoctorId(0L);

        when(patientClient.getPatient(1L)).thenReturn(patient);

        assertThrows(IllegalArgumentException.class, () -> service.createOrder(request));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void uploadResult_shouldUpload_whenValid() {
        LabOrder order = LabOrder.builder()
                .id(1L)
                .patientId(1L)
                .doctorId(2L)
                .examType("Hemograma")
                .status("PENDIENTE")
                .build();

        ResultRequestDTO resultRequest = new ResultRequestDTO();
        resultRequest.setFindings("Hemoglobina baja");
        resultRequest.setDocumentUrl("http://docs.com/result.pdf");

        PatientResponseDTO patient = buildPatientResponse();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(resultRepository.findByOrderId(1L)).thenReturn(Optional.empty());
        when(patientClient.getPatient(1L)).thenReturn(patient);
        doNothing().when(notificationClient).sendNotification(any(NotificationRequestDTO.class));

        service.uploadResult(1L, resultRequest);

        verify(resultRepository).save(any(LabResult.class));
        verify(orderRepository).save(order);
        assertEquals("COMPLETADO", order.getStatus());
        verify(notificationClient).sendNotification(any(NotificationRequestDTO.class));
    }

    @Test
    void uploadResult_shouldThrowNotFoundException_whenOrderNotFound() {
        ResultRequestDTO resultRequest = new ResultRequestDTO();
        resultRequest.setFindings("Hemoglobina baja");
        resultRequest.setDocumentUrl("http://docs.com/result.pdf");

        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(LaboratoryNotFoundException.class,
                () -> service.uploadResult(99L, resultRequest));

        verify(resultRepository, never()).save(any());
    }

    @Test
    void uploadResult_shouldThrowException_whenOrderCancelled() {
        LabOrder order = LabOrder.builder()
                .id(1L)
                .patientId(1L)
                .status("CANCELADA")
                .build();

        ResultRequestDTO resultRequest = new ResultRequestDTO();
        resultRequest.setFindings("Hemoglobina baja");
        resultRequest.setDocumentUrl("http://docs.com/result.pdf");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class,
                () -> service.uploadResult(1L, resultRequest));

        verify(resultRepository, never()).save(any());
    }

    @Test
    void getPatientOrders_shouldReturnOrders_whenValid() {
        PatientResponseDTO patient = buildPatientResponse();

        List<LabOrder> orders = List.of(
                LabOrder.builder().id(1L).patientId(1L).doctorId(2L).examType("Hemograma").status("PENDIENTE").build(),
                LabOrder.builder().id(2L).patientId(1L).doctorId(2L).examType("Glucosa").status("COMPLETADO").build()
        );

        when(patientClient.getPatient(1L)).thenReturn(patient);
        when(orderRepository.findByPatientId(1L)).thenReturn(orders);

        List<LabOrderResponseDTO> result = service.getPatientOrders(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Hemograma", result.get(0).getExamType());
        assertEquals("Glucosa", result.get(1).getExamType());
        assertEquals("Juan Pérez", result.get(0).getPatientName());
    }

    @Test
    void getPatientOrders_shouldThrowException_whenPatientNotFound() {
        doThrow(new RuntimeException("Paciente no encontrado")).when(patientClient).getPatient(99L);

        assertThrows(IllegalArgumentException.class,
                () -> service.getPatientOrders(99L));

        verify(orderRepository, never()).findByPatientId(any());
    }
}
