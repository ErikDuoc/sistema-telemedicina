package cl.duoc.fullstack.doctorservice.config;

import cl.duoc.fullstack.doctorservice.model.Doctor;
import cl.duoc.fullstack.doctorservice.model.Specialty;
import cl.duoc.fullstack.doctorservice.repository.DoctorRepository;
import cl.duoc.fullstack.doctorservice.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public void run(String... args) {

        if (specialtyRepository.count() == 0) {
            log.info("Inicializando especialidades médicas...");

            List<Specialty> specialties = List.of(
                Specialty.builder()
                        .name("Medicina General")
                        .description("Médico de atención primaria y diagnóstico general")
                        .build(),
                Specialty.builder()
                        .name("Cardiología")
                        .description("Especialista en enfermedades del corazón")
                        .build(),
                Specialty.builder()
                        .name("Neurología")
                        .description("Especialista en enfermedades del sistema nervioso")
                        .build(),
                Specialty.builder()
                        .name("Pediatría")
                        .description("Especialista en salud infantil")
                        .build(),
                Specialty.builder()
                        .name("Traumatología")
                        .description("Especialista en lesiones y enfermedades del sistema óseo")
                        .build()
            );

            specialtyRepository.saveAll(specialties);
            log.info("✅ {} especialidades creadas", specialtyRepository.count());
        }

        if (doctorRepository.count() == 0) {
            log.info("Inicializando doctores de ejemplo...");

            List<Specialty> specialties = specialtyRepository.findAll();

            List<Doctor> doctors = List.of(
                Doctor.builder()
                        .firstName("Carlos")
                        .lastName("Méndez Rodríguez")
                        .nationalRegistry("12.345.678-K")
                        .email("carlos.mendez@clinica.cl")
                        .specialty(specialties.stream().filter(s -> s.getName().equals("Cardiología")).findFirst().orElse(null))
                        .build(),
                Doctor.builder()
                        .firstName("Alejandra")
                        .lastName("González Silva")
                        .nationalRegistry("13.456.789-1")
                        .email("alejandra.gonzalez@clinica.cl")
                        .specialty(specialties.stream().filter(s -> s.getName().equals("Medicina General")).findFirst().orElse(null))
                        .build(),
                Doctor.builder()
                        .firstName("Roberto")
                        .lastName("Fuentes Díaz")
                        .nationalRegistry("14.567.890-2")
                        .email("roberto.fuentes@clinica.cl")
                        .specialty(specialties.stream().filter(s -> s.getName().equals("Neurología")).findFirst().orElse(null))
                        .build(),
                Doctor.builder()
                        .firstName("Marcela")
                        .lastName("López Martínez")
                        .nationalRegistry("15.678.901-3")
                        .email("marcela.lopez@clinica.cl")
                        .specialty(specialties.stream().filter(s -> s.getName().equals("Pediatría")).findFirst().orElse(null))
                        .build(),
                Doctor.builder()
                        .firstName("Javier")
                        .lastName("Torres Acuña")
                        .nationalRegistry("16.789.012-4")
                        .email("javier.torres@clinica.cl")
                        .specialty(specialties.stream().filter(s -> s.getName().equals("Traumatología")).findFirst().orElse(null))
                        .build(),
                Doctor.builder()
                        .firstName("Sofía")
                        .lastName("Valenzuela Flores")
                        .nationalRegistry("17.890.123-5")
                        .email("sofia.valenzuela@clinica.cl")
                        .specialty(specialties.stream().filter(s -> s.getName().equals("Medicina General")).findFirst().orElse(null))
                        .build()
            );

            doctorRepository.saveAll(doctors);
            log.info("✅ {} doctores creados", doctorRepository.count());
        }
    }
}
