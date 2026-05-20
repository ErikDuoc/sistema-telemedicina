package cl.duoc.fullstack.appointmentservice.config;

import cl.duoc.fullstack.appointmentservice.model.Appointment;
import cl.duoc.fullstack.appointmentservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AppointmentRepository appointmentRepository;

    @Override
    public void run(String... args) {
        if (appointmentRepository.count() == 0) {
            log.info("Inicializando citas médicas de ejemplo...");

            LocalDate today = LocalDate.now();

            List<Appointment> appointments = List.of(
                Appointment.builder()
                        .patientId(1L)
                        .doctorId(1L)
                        .date(today.plusDays(3).toString())
                        .time("09:00")
                        .status("PENDING")
                        .build(),
                Appointment.builder()
                        .patientId(2L)
                        .doctorId(2L)
                        .date(today.plusDays(5).toString())
                        .time("10:30")
                        .status("PENDING")
                        .build(),
                Appointment.builder()
                        .patientId(3L)
                        .doctorId(3L)
                        .date(today.plusDays(7).toString())
                        .time("14:00")
                        .status("CONFIRMED")
                        .build(),
                Appointment.builder()
                        .patientId(4L)
                        .doctorId(4L)
                        .date(today.plusDays(2).toString())
                        .time("11:00")
                        .status("CONFIRMED")
                        .build(),
                Appointment.builder()
                        .patientId(5L)
                        .doctorId(5L)
                        .date(today.plusDays(10).toString())
                        .time("15:30")
                        .status("PENDING")
                        .build(),
                Appointment.builder()
                        .patientId(6L)
                        .doctorId(1L)
                        .date(today.plusDays(8).toString())
                        .time("10:00")
                        .status("COMPLETED")
                        .build()
            );

            appointmentRepository.saveAll(appointments);
            log.info("✅ {} citas creadas", appointmentRepository.count());
        }
    }
}

