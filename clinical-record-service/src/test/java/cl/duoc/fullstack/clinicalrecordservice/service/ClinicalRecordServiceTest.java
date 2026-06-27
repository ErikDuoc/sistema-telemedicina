package cl.duoc.fullstack.clinicalrecordservice.service;

import cl.duoc.fullstack.clinicalrecordservice.client.AppointmentClient;
import cl.duoc.fullstack.clinicalrecordservice.dto.ClinicalRecordRequestDTO;
import cl.duoc.fullstack.clinicalrecordservice.dto.ClinicalRecordResponseDTO;
import cl.duoc.fullstack.clinicalrecordservice.exception.ClinicalRecordNotFoundException;
import cl.duoc.fullstack.clinicalrecordservice.exception.DuplicateClinicalRecordException;
import cl.duoc.fullstack.clinicalrecordservice.model.ClinicalRecord;
import cl.duoc.fullstack.clinicalrecordservice.repository.ClinicalRecordRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicalRecordServiceTest {

    @Mock
    private ClinicalRecordRepository repository;

    @Mock
    private AppointmentClient appointmentClient;

    @InjectMocks
    private ClinicalRecordService service;

    private ClinicalRecordRequestDTO buildValidRequest() {
        ClinicalRecordRequestDTO dto = new ClinicalRecordRequestDTO();
        dto.setAppointmentId(1L);
        dto.setPatientId(5L);
        dto.setDoctorId(3L);
        dto.setDiagnosis("Hipertensión arterial");
        dto.setTreatment("Medicación antihipertensiva");
        return dto;
    }

    @Test
    void create_shouldReturnRecord_whenDataIsValid() {
        ClinicalRecordRequestDTO request = buildValidRequest();
        ClinicalRecord savedRecord = ClinicalRecord.builder()
                .id(1L)
                .appointmentId(1L)
                .patientId(5L)
                .doctorId(3L)
                .diagnosis("Hipertensión arterial")
                .treatment("Medicación antihipertensiva")
                .createdAt("2025-01-01T10:00:00")
                .build();

        when(appointmentClient.validateAppointment(5L)).thenReturn(new Object());
        when(repository.findByAppointmentId(1L)).thenReturn(Optional.empty());
        when(repository.save(any(ClinicalRecord.class))).thenReturn(savedRecord);

        ClinicalRecordResponseDTO result = service.create(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getAppointmentId());
        assertEquals(5L, result.getPatientId());
        assertEquals(3L, result.getDoctorId());
        assertEquals("Hipertensión arterial", result.getDiagnosis());
        assertEquals("Medicación antihipertensiva", result.getTreatment());

        verify(appointmentClient).validateAppointment(5L);
        verify(repository).findByAppointmentId(1L);
        verify(repository).save(any(ClinicalRecord.class));
    }

    @Test
    void create_shouldThrowException_whenAppointmentNotFound() {
        ClinicalRecordRequestDTO request = buildValidRequest();

        doThrow(mock(FeignException.class)).when(appointmentClient).validateAppointment(5L);

        assertThrows(FeignException.class, () -> service.create(request));

        verify(repository, never()).save(any());
    }

    @Test
    void create_shouldThrowDuplicateException_whenAppointmentAlreadyHasRecord() {
        ClinicalRecordRequestDTO request = buildValidRequest();
        ClinicalRecord existingRecord = ClinicalRecord.builder().id(99L).appointmentId(1L).build();

        when(appointmentClient.validateAppointment(5L)).thenReturn(new Object());
        when(repository.findByAppointmentId(1L)).thenReturn(Optional.of(existingRecord));

        DuplicateClinicalRecordException ex = assertThrows(DuplicateClinicalRecordException.class,
                () -> service.create(request));
        assertTrue(ex.getMessage().contains("already exists"));

        verify(repository, never()).save(any());
    }

    @Test
    void getById_shouldReturnRecord_whenExists() {
        ClinicalRecord record = ClinicalRecord.builder()
                .id(1L)
                .appointmentId(1L)
                .patientId(5L)
                .doctorId(3L)
                .diagnosis("Diagnóstico")
                .treatment("Tratamiento")
                .createdAt("2025-01-01T10:00:00")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(record));

        ClinicalRecordResponseDTO result = service.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Diagnóstico", result.getDiagnosis());
    }

    @Test
    void getById_shouldThrowNotFoundException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ClinicalRecordNotFoundException.class, () -> service.getById(99L));
    }

    @Test
    void create_shouldSetCreatedAtTimestamp() {
        ClinicalRecordRequestDTO request = buildValidRequest();
        ClinicalRecord savedRecord = ClinicalRecord.builder()
                .id(1L)
                .appointmentId(1L)
                .patientId(5L)
                .doctorId(3L)
                .diagnosis("Hipertensión arterial")
                .treatment("Medicación antihipertensiva")
                .createdAt("2025-01-01T10:00:00")
                .build();

        when(appointmentClient.validateAppointment(5L)).thenReturn(new Object());
        when(repository.findByAppointmentId(1L)).thenReturn(Optional.empty());
        when(repository.save(any(ClinicalRecord.class))).thenAnswer(invocation -> {
            ClinicalRecord rec = invocation.getArgument(0);
            assertNotNull(rec.getCreatedAt());
            return savedRecord;
        });

        service.create(request);
    }

    @Test
    void create_shouldMapAllFieldsCorrectly() {
        ClinicalRecordRequestDTO request = buildValidRequest();
        ClinicalRecord savedRecord = ClinicalRecord.builder()
                .id(1L)
                .appointmentId(1L)
                .patientId(5L)
                .doctorId(3L)
                .diagnosis("Hipertensión arterial")
                .treatment("Medicación antihipertensiva")
                .createdAt("2025-01-01T10:00:00")
                .build();

        when(appointmentClient.validateAppointment(5L)).thenReturn(new Object());
        when(repository.findByAppointmentId(1L)).thenReturn(Optional.empty());
        when(repository.save(any(ClinicalRecord.class))).thenReturn(savedRecord);

        ClinicalRecordResponseDTO result = service.create(request);

        assertEquals(savedRecord.getId(), result.getId());
        assertEquals(savedRecord.getAppointmentId(), result.getAppointmentId());
        assertEquals(savedRecord.getPatientId(), result.getPatientId());
        assertEquals(savedRecord.getDoctorId(), result.getDoctorId());
        assertEquals(savedRecord.getDiagnosis(), result.getDiagnosis());
        assertEquals(savedRecord.getTreatment(), result.getTreatment());
        assertEquals(savedRecord.getCreatedAt(), result.getCreatedAt());
    }
}
