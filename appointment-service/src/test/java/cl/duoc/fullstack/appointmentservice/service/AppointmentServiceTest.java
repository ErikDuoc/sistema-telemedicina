package cl.duoc.fullstack.appointmentservice.service;

import cl.duoc.fullstack.appointmentservice.client.AgendaClient;
import cl.duoc.fullstack.appointmentservice.client.DoctorClient;
import cl.duoc.fullstack.appointmentservice.client.PatientClient;
import cl.duoc.fullstack.appointmentservice.dto.AppointmentRequest;
import cl.duoc.fullstack.appointmentservice.dto.AppointmentResponseDTO;
import cl.duoc.fullstack.appointmentservice.exception.AppointmentNotFoundException;
import cl.duoc.fullstack.appointmentservice.exception.DuplicateAppointmentException;
import cl.duoc.fullstack.appointmentservice.model.Appointment;
import cl.duoc.fullstack.appointmentservice.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private PatientClient patientClient;

    @Mock
    private DoctorClient doctorClient;

    @Mock
    private AgendaClient agendaClient;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void create_shouldReturnAppointment_whenDataIsValid() {
        String futureDate = LocalDate.now().plusDays(5).toString();

        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(1L);
        request.setDoctorId(1L);
        request.setDate(futureDate);
        request.setTime("10:00");

        Appointment savedAppointment = Appointment.builder()
                .id(1L)
                .patientId(1L)
                .doctorId(1L)
                .date(futureDate)
                .time("10:00")
                .status("PENDING")
                .build();

        when(patientClient.getPatient(1L)).thenReturn(new Object());
        when(doctorClient.getDoctor(1L)).thenReturn(new Object());
        when(agendaClient.getDoctorAgenda(1L)).thenReturn(new Object());
        when(repository.findByPatientId(1L)).thenReturn(List.of());
        when(repository.save(any(Appointment.class))).thenReturn(savedAppointment);

        AppointmentResponseDTO result = appointmentService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPatientId()).isEqualTo(1L);
        assertThat(result.getDoctorId()).isEqualTo(1L);
        assertThat(result.getDate()).isEqualTo(futureDate);
        assertThat(result.getTime()).isEqualTo("10:00");
        assertThat(result.getStatus()).isEqualTo("PENDING");

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("PENDING");
    }

    @Test
    void create_shouldThrowException_whenPatientNotFound() {
        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(99L);
        request.setDoctorId(1L);
        request.setDate(LocalDate.now().plusDays(1).toString());
        request.setTime("10:00");

        when(patientClient.getPatient(99L)).thenThrow(new RuntimeException("Patient not found"));

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("paciente");
    }

    @Test
    void create_shouldThrowException_whenDoctorNotFound() {
        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(1L);
        request.setDoctorId(99L);
        request.setDate(LocalDate.now().plusDays(1).toString());
        request.setTime("10:00");

        when(patientClient.getPatient(1L)).thenReturn(new Object());
        when(doctorClient.getDoctor(99L)).thenThrow(new RuntimeException("Doctor not found"));

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("doctor");
    }

    @Test
    void create_shouldThrowException_whenNoAgenda() {
        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(1L);
        request.setDoctorId(1L);
        request.setDate(LocalDate.now().plusDays(1).toString());
        request.setTime("10:00");

        when(patientClient.getPatient(1L)).thenReturn(new Object());
        when(doctorClient.getDoctor(1L)).thenReturn(new Object());
        when(agendaClient.getDoctorAgenda(1L)).thenThrow(new RuntimeException("No agenda"));

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("agenda");
    }

    @Test
    void create_shouldThrowException_whenPastDate() {
        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(1L);
        request.setDoctorId(1L);
        request.setDate(LocalDate.now().minusDays(1).toString());
        request.setTime("10:00");

        when(patientClient.getPatient(1L)).thenReturn(new Object());
        when(doctorClient.getDoctor(1L)).thenReturn(new Object());
        when(agendaClient.getDoctorAgenda(1L)).thenReturn(new Object());

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fechas pasadas");
    }

    @Test
    void create_shouldThrowDuplicateException_whenDuplicate() {
        String futureDate = LocalDate.now().plusDays(5).toString();

        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(1L);
        request.setDoctorId(1L);
        request.setDate(futureDate);
        request.setTime("10:00");

        Appointment existing = Appointment.builder()
                .id(1L)
                .patientId(1L)
                .doctorId(1L)
                .date(futureDate)
                .time("10:00")
                .status("PENDING")
                .build();

        when(patientClient.getPatient(1L)).thenReturn(new Object());
        when(doctorClient.getDoctor(1L)).thenReturn(new Object());
        when(agendaClient.getDoctorAgenda(1L)).thenReturn(new Object());
        when(repository.findByPatientId(1L)).thenReturn(List.of(existing));

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(DuplicateAppointmentException.class)
                .hasMessageContaining("Ya existe");
    }

    @Test
    void getByPatient_shouldReturnList() {
        Appointment appointment1 = Appointment.builder()
                .id(1L).patientId(1L).doctorId(1L).date("2026-07-10").time("10:00").status("PENDING").build();
        Appointment appointment2 = Appointment.builder()
                .id(2L).patientId(1L).doctorId(2L).date("2026-07-15").time("14:00").status("CONFIRMED").build();

        when(repository.findByPatientId(1L)).thenReturn(List.of(appointment1, appointment2));

        List<Appointment> result = appointmentService.getByPatient(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStatus()).isEqualTo("PENDING");
        assertThat(result.get(1).getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    void updateStatus_shouldUpdateAndReturn() {
        Appointment existing = Appointment.builder()
                .id(1L).patientId(1L).doctorId(1L).date("2026-07-10").time("10:00").status("PENDING").build();

        Appointment updated = Appointment.builder()
                .id(1L).patientId(1L).doctorId(1L).date("2026-07-10").time("10:00").status("CONFIRMED").build();

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Appointment.class))).thenReturn(updated);

        AppointmentResponseDTO result = appointmentService.updateStatus(1L, "CONFIRMED");

        assertThat(result.getStatus()).isEqualTo("CONFIRMED");

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    void updateStatus_shouldThrowException_whenInvalidStatus() {
        assertThatThrownBy(() -> appointmentService.updateStatus(1L, "INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estado");
    }

    @Test
    void updateStatus_shouldThrowNotFoundException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.updateStatus(99L, "CONFIRMED"))
                .isInstanceOf(AppointmentNotFoundException.class)
                .hasMessageContaining("Cita no encontrada");
    }
}
