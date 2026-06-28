package cl.duoc.fullstack.doctorservice.service;

import cl.duoc.fullstack.doctorservice.dto.DoctorRequestDTO;
import cl.duoc.fullstack.doctorservice.dto.DoctorResponseDTO;
import cl.duoc.fullstack.doctorservice.exception.DoctorNotFoundException;
import cl.duoc.fullstack.doctorservice.exception.DuplicateDoctorException;
import cl.duoc.fullstack.doctorservice.exception.SpecialtyNotFoundException;
import cl.duoc.fullstack.doctorservice.model.Doctor;
import cl.duoc.fullstack.doctorservice.model.Specialty;
import cl.duoc.fullstack.doctorservice.repository.DoctorRepository;
import cl.duoc.fullstack.doctorservice.repository.SpecialtyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void createDoctor_shouldReturnDoctorResponse_whenDataIsValid() {
        Specialty specialty = Specialty.builder()
                .id(1L)
                .name("Cardiología")
                .build();

        DoctorRequestDTO dto = new DoctorRequestDTO();
        dto.setFirstName("Juan");
        dto.setLastName("Pérez");
        dto.setNationalRegistry("11.111.111-1");
        dto.setEmail("juan@mail.com");
        dto.setSpecialtyId(1L);

        Doctor doctorToSave = Doctor.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .nationalRegistry(dto.getNationalRegistry())
                .email(dto.getEmail())
                .specialty(specialty)
                .build();

        Doctor savedDoctor = Doctor.builder()
                .id(1L)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .nationalRegistry(dto.getNationalRegistry())
                .email(dto.getEmail())
                .specialty(specialty)
                .build();

        when(doctorRepository.existsByNationalRegistry(dto.getNationalRegistry())).thenReturn(false);
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);

        DoctorResponseDTO result = doctorService.createDoctor(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Juan");
        assertThat(result.getLastName()).isEqualTo("Pérez");
        assertThat(result.getNationalRegistry()).isEqualTo("11.111.111-1");
        assertThat(result.getEmail()).isEqualTo("juan@mail.com");
        assertThat(result.getSpecialtyName()).isEqualTo("Cardiología");

        ArgumentCaptor<Doctor> captor = ArgumentCaptor.forClass(Doctor.class);
        verify(doctorRepository).save(captor.capture());
        assertThat(captor.getValue().getSpecialty()).isEqualTo(specialty);
    }

    @Test
    void createDoctor_shouldThrowDuplicateException_whenRegistryExists() {
        DoctorRequestDTO dto = new DoctorRequestDTO();
        dto.setNationalRegistry("11.111.111-1");

        when(doctorRepository.existsByNationalRegistry(dto.getNationalRegistry())).thenReturn(true);

        assertThatThrownBy(() -> doctorService.createDoctor(dto))
                .isInstanceOf(DuplicateDoctorException.class)
                .hasMessageContaining("national registry");

        verify(doctorRepository).existsByNationalRegistry(dto.getNationalRegistry());
        verifyNoInteractions(specialtyRepository);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void createDoctor_shouldThrowSpecialtyNotFoundException_whenSpecialtyDoesNotExist() {
        DoctorRequestDTO dto = new DoctorRequestDTO();
        dto.setFirstName("Ana");
        dto.setLastName("López");
        dto.setNationalRegistry("22.222.222-2");
        dto.setSpecialtyId(99L);

        when(doctorRepository.existsByNationalRegistry(dto.getNationalRegistry())).thenReturn(false);
        when(specialtyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.createDoctor(dto))
                .isInstanceOf(SpecialtyNotFoundException.class)
                .hasMessageContaining("99");

        verify(doctorRepository).existsByNationalRegistry(dto.getNationalRegistry());
        verify(specialtyRepository).findById(99L);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void getAllDoctors_shouldReturnList() {
        Specialty cardiologia = Specialty.builder().id(1L).name("Cardiología").build();
        Specialty neurologia = Specialty.builder().id(2L).name("Neurología").build();

        Doctor doctor1 = Doctor.builder()
                .id(1L).firstName("Juan").lastName("Pérez")
                .nationalRegistry("11.111.111-1").email("juan@mail.com")
                .specialty(cardiologia).build();

        Doctor doctor2 = Doctor.builder()
                .id(2L).firstName("Ana").lastName("López")
                .nationalRegistry("22.222.222-2").email("ana@mail.com")
                .specialty(neurologia).build();

        when(doctorRepository.findAll()).thenReturn(List.of(doctor1, doctor2));

        List<DoctorResponseDTO> result = doctorService.getAllDoctors();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Juan");
        assertThat(result.get(1).getFirstName()).isEqualTo("Ana");
    }

    @Test
    void getDoctorById_shouldReturnDoctor_whenExists() {
        Specialty specialty = Specialty.builder().id(1L).name("Cardiología").build();
        Doctor doctor = Doctor.builder()
                .id(1L).firstName("Juan").lastName("Pérez")
                .nationalRegistry("11.111.111-1").email("juan@mail.com")
                .specialty(specialty).build();

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        DoctorResponseDTO result = doctorService.getDoctorById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSpecialtyName()).isEqualTo("Cardiología");
    }

    @Test
    void getDoctorById_shouldThrowNotFoundException_whenNotFound() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.getDoctorById(99L))
                .isInstanceOf(DoctorNotFoundException.class)
                .hasMessageContaining("99");

        verify(doctorRepository).findById(99L);
    }
}
