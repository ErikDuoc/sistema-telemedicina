package cl.duoc.fullstack.agendaservice.service;

import cl.duoc.fullstack.agendaservice.client.DoctorClient;
import cl.duoc.fullstack.agendaservice.dto.AvailabilityRequestDTO;
import cl.duoc.fullstack.agendaservice.dto.AvailabilityResponseDTO;
import cl.duoc.fullstack.agendaservice.dto.DoctorResponseDTO;
import cl.duoc.fullstack.agendaservice.exception.AgendaNotFoundException;
import cl.duoc.fullstack.agendaservice.exception.DuplicateAgendaException;
import cl.duoc.fullstack.agendaservice.model.Availability;
import cl.duoc.fullstack.agendaservice.repository.AvailabilityRepository;
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
class AgendaServiceTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private DoctorClient doctorClient;

    @InjectMocks
    private AgendaService agendaService;

    @Test
    void create_shouldReturnAvailability_whenDataIsValid() {
        DoctorResponseDTO doctorDto = new DoctorResponseDTO();
        doctorDto.setId(1L);
        doctorDto.setFirstName("Juan");
        doctorDto.setLastName("Pérez");

        AvailabilityRequestDTO request = new AvailabilityRequestDTO();
        request.setDoctorId(1L);
        request.setDayOfWeek("MONDAY");
        request.setStartTime("09:00");
        request.setEndTime("17:00");

        Availability savedAvailability = Availability.builder()
                .id(1L)
                .doctorId(1L)
                .dayOfWeek("MONDAY")
                .startTime("09:00")
                .endTime("17:00")
                .build();

        when(doctorClient.getDoctor(1L)).thenReturn(doctorDto);
        when(availabilityRepository.findByDoctorIdAndDayOfWeek(1L, "MONDAY")).thenReturn(Optional.empty());
        when(availabilityRepository.save(any(Availability.class))).thenReturn(savedAvailability);

        AvailabilityResponseDTO result = agendaService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDoctorId()).isEqualTo(1L);
        assertThat(result.getDoctorName()).isEqualTo("Juan Pérez");
        assertThat(result.getDayOfWeek()).isEqualTo("MONDAY");
        assertThat(result.getStartTime()).isEqualTo("09:00");
        assertThat(result.getEndTime()).isEqualTo("17:00");

        ArgumentCaptor<Availability> captor = ArgumentCaptor.forClass(Availability.class);
        verify(availabilityRepository).save(captor.capture());
        assertThat(captor.getValue().getDoctorId()).isEqualTo(1L);
        assertThat(captor.getValue().getDayOfWeek()).isEqualTo("MONDAY");
    }

    @Test
    void create_shouldThrowDuplicateException_whenAvailabilityExists() {
        DoctorResponseDTO doctorDto = new DoctorResponseDTO();
        doctorDto.setId(1L);
        doctorDto.setFirstName("Juan");
        doctorDto.setLastName("Pérez");

        AvailabilityRequestDTO request = new AvailabilityRequestDTO();
        request.setDoctorId(1L);
        request.setDayOfWeek("MONDAY");
        request.setStartTime("09:00");
        request.setEndTime("17:00");

        Availability existing = Availability.builder().id(1L).build();

        when(doctorClient.getDoctor(1L)).thenReturn(doctorDto);
        when(availabilityRepository.findByDoctorIdAndDayOfWeek(1L, "MONDAY")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> agendaService.create(request))
                .isInstanceOf(DuplicateAgendaException.class)
                .hasMessageContaining("MONDAY");

        verify(availabilityRepository).findByDoctorIdAndDayOfWeek(1L, "MONDAY");
        verifyNoMoreInteractions(availabilityRepository);
    }

    @Test
    void getByDoctor_shouldReturnList_whenAvailabilitiesExist() {
        DoctorResponseDTO doctorDto = new DoctorResponseDTO();
        doctorDto.setId(1L);
        doctorDto.setFirstName("Ana");
        doctorDto.setLastName("López");

        Availability availability1 = Availability.builder()
                .id(1L).doctorId(1L).dayOfWeek("MONDAY").startTime("09:00").endTime("13:00").build();
        Availability availability2 = Availability.builder()
                .id(2L).doctorId(1L).dayOfWeek("WEDNESDAY").startTime("14:00").endTime("18:00").build();

        when(doctorClient.getDoctor(1L)).thenReturn(doctorDto);
        when(availabilityRepository.findByDoctorId(1L)).thenReturn(List.of(availability1, availability2));

        List<AvailabilityResponseDTO> result = agendaService.getByDoctor(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDayOfWeek()).isEqualTo("MONDAY");
        assertThat(result.get(1).getDayOfWeek()).isEqualTo("WEDNESDAY");
        assertThat(result.get(0).getDoctorName()).isEqualTo("Ana López");
    }

    @Test
    void getByDoctor_shouldThrowNotFoundException_whenNoAvailabilities() {
        DoctorResponseDTO doctorDto = new DoctorResponseDTO();
        doctorDto.setId(1L);
        doctorDto.setFirstName("Ana");
        doctorDto.setLastName("López");

        when(doctorClient.getDoctor(1L)).thenReturn(doctorDto);
        when(availabilityRepository.findByDoctorId(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> agendaService.getByDoctor(1L))
                .isInstanceOf(AgendaNotFoundException.class)
                .hasMessageContaining("1");

        verify(availabilityRepository).findByDoctorId(1L);
    }

    @Test
    void getByDoctor_shouldPropagateException_whenDoctorNotFound() {
        when(doctorClient.getDoctor(99L)).thenThrow(new RuntimeException("Doctor not found"));

        assertThatThrownBy(() -> agendaService.getByDoctor(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Doctor not found");
    }

    @Test
    void create_shouldPropagateException_whenDoctorNotFound() {
        AvailabilityRequestDTO request = new AvailabilityRequestDTO();
        request.setDoctorId(99L);
        request.setDayOfWeek("MONDAY");
        request.setStartTime("09:00");
        request.setEndTime("17:00");

        when(doctorClient.getDoctor(99L)).thenThrow(new RuntimeException("Doctor not found with id: 99"));

        assertThatThrownBy(() -> agendaService.create(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Doctor not found with id: 99");
    }
}
