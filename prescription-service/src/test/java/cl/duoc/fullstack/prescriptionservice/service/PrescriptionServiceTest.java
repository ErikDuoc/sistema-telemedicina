package cl.duoc.fullstack.prescriptionservice.service;

import cl.duoc.fullstack.prescriptionservice.client.ClinicalRecordClient;
import cl.duoc.fullstack.prescriptionservice.dto.PrescriptionRequestDTO;
import cl.duoc.fullstack.prescriptionservice.dto.PrescriptionResponseDTO;
import cl.duoc.fullstack.prescriptionservice.exception.PrescriptionNotFoundException;
import cl.duoc.fullstack.prescriptionservice.model.Prescription;
import cl.duoc.fullstack.prescriptionservice.repository.PrescriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository repository;

    @Mock
    private ClinicalRecordClient clinicalRecordClient;

    @InjectMocks
    private PrescriptionService service;

    private PrescriptionRequestDTO buildValidRequest() {
        PrescriptionRequestDTO dto = new PrescriptionRequestDTO();
        dto.setClinicalRecordId(1L);
        dto.setPatientId(1L);
        dto.setMedication("Ibuprofeno 400mg");
        dto.setIndications("1 comprimido cada 8 horas");
        return dto;
    }

    private Prescription buildPrescription(Long id) {
        return Prescription.builder()
                .id(id)
                .clinicalRecordId(1L)
                .patientId(1L)
                .medication("Ibuprofeno 400mg")
                .indications("1 comprimido cada 8 horas")
                .createdAt(LocalDateTime.now().toString())
                .build();
    }

    @Test
    void create_shouldReturnPrescription_whenDataIsValid() {
        PrescriptionRequestDTO request = buildValidRequest();
        Prescription saved = buildPrescription(1L);

        when(clinicalRecordClient.getClinicalRecord(1L)).thenReturn(new Object());
        when(repository.save(any(Prescription.class))).thenReturn(saved);

        PrescriptionResponseDTO result = service.create(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getClinicalRecordId());
        assertEquals("Ibuprofeno 400mg", result.getMedication());
        assertNotNull(result.getCreatedAt());

        verify(clinicalRecordClient).getClinicalRecord(1L);
        verify(repository).save(any(Prescription.class));
    }

    @Test
    void create_shouldThrowException_whenClinicalRecordNotFound() {
        PrescriptionRequestDTO request = buildValidRequest();

        when(clinicalRecordClient.getClinicalRecord(1L)).thenThrow(new RuntimeException("Not found"));

        assertThrows(IllegalArgumentException.class, () -> service.create(request));

        verify(repository, never()).save(any());
    }

    @Test
    void create_shouldThrowException_whenInvalidMedication() {
        PrescriptionRequestDTO request = buildValidRequest();
        request.setMedication(null);

        assertThrows(IllegalArgumentException.class, () -> service.create(request));
        verify(repository, never()).save(any());
    }

    @Test
    void create_shouldThrowException_whenEmptyIndications() {
        PrescriptionRequestDTO request = buildValidRequest();
        request.setIndications("");

        assertThrows(IllegalArgumentException.class, () -> service.create(request));
        verify(repository, never()).save(any());
    }

    @Test
    void getById_shouldReturnPrescription_whenExists() {
        Prescription prescription = buildPrescription(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(prescription));

        PrescriptionResponseDTO result = service.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getClinicalRecordId());
        assertEquals("Ibuprofeno 400mg", result.getMedication());
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PrescriptionNotFoundException.class, () -> service.getById(99L));
    }
}
