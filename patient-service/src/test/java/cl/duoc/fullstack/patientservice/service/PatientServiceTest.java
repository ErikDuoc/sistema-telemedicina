package cl.duoc.fullstack.patientservice.service;

import cl.duoc.fullstack.patientservice.dto.*;
import cl.duoc.fullstack.patientservice.exception.DuplicateResourceException;
import cl.duoc.fullstack.patientservice.exception.ResourceNotFoundException;
import cl.duoc.fullstack.patientservice.model.EmergencyContact;
import cl.duoc.fullstack.patientservice.model.Patient;
import cl.duoc.fullstack.patientservice.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository repository;

    @InjectMocks
    private PatientService service;

    private EmergencyContactDTO buildContactDTO() {
        return new EmergencyContactDTO(null, "María González", "Madre", "+56912345678");
    }

    private PatientRequestDTO buildValidRequest() {
        return new PatientRequestDTO(
                "20.123.456-7",
                "Carlos",
                "González Martínez",
                LocalDate.of(1990, 5, 15),
                "Masculino",
                "carlos@email.com",
                "FONASA",
                List.of(buildContactDTO())
        );
    }

    private Patient buildPatient(Long id) {
        Patient patient = Patient.builder()
                .id(id)
                .rut("20.123.456-7")
                .nombre("Carlos")
                .apellido("González Martínez")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .genero("Masculino")
                .email("carlos@email.com")
                .prevision("FONASA")
                .build();
        EmergencyContact contact = EmergencyContact.builder()
                .id(1L)
                .nombre("María González")
                .parentesco("Madre")
                .telefono("+56912345678")
                .patient(patient)
                .build();
        patient.setContactosEmergencia(new ArrayList<>(List.of(contact)));
        return patient;
    }

    @Test
    void findAll_shouldReturnList_whenPatientsExist() {
        Patient patient = buildPatient(1L);
        when(repository.findAll()).thenReturn(List.of(patient));

        List<PatientResponseDTO> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Carlos", result.get(0).nombre());
    }

    @Test
    void findById_shouldReturnPatient_whenExists() {
        Patient patient = buildPatient(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(patient));

        PatientResponseDTO result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("carlos@email.com", result.email());
        assertEquals(1, result.contactosEmergencia().size());
    }

    @Test
    void findById_shouldThrowNotFoundException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void create_shouldReturnPatient_whenDataIsValid() {
        PatientRequestDTO request = buildValidRequest();
        Patient savedPatient = buildPatient(1L);

        when(repository.existsByRutIgnoreCase("20.123.456-7")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("carlos@email.com")).thenReturn(false);
        when(repository.save(any(Patient.class))).thenReturn(savedPatient);

        PatientResponseDTO result = service.create(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Carlos", result.nombre());
        assertEquals("FONASA", result.prevision());

        verify(repository).existsByRutIgnoreCase("20.123.456-7");
        verify(repository).existsByEmailIgnoreCase("carlos@email.com");
        verify(repository).save(any(Patient.class));
    }

    @Test
    void create_shouldThrowException_whenDuplicateRut() {
        PatientRequestDTO request = buildValidRequest();

        when(repository.existsByRutIgnoreCase("20.123.456-7")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(request));

        verify(repository, never()).save(any());
    }

    @Test
    void update_shouldReturnUpdatedPatient_whenExists() {
        PatientRequestDTO request = buildValidRequest();
        Patient existingPatient = buildPatient(1L);
        Patient updatedPatient = buildPatient(1L);
        updatedPatient.setNombre("Carlos Updated");

        when(repository.findById(1L)).thenReturn(Optional.of(existingPatient));
        when(repository.save(any(Patient.class))).thenReturn(updatedPatient);

        PatientResponseDTO result = service.update(1L, request);

        assertNotNull(result);
        assertEquals("Carlos Updated", result.nombre());
        verify(repository).save(any(Patient.class));
    }

    @Test
    void update_shouldThrowNotFoundException_whenNotFound() {
        PatientRequestDTO request = buildValidRequest();

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(99L, request));

        verify(repository, never()).save(any());
    }

    @Test
    void delete_shouldDelete_whenExists() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowNotFoundException_whenNotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));

        verify(repository, never()).deleteById(any());
    }
}
