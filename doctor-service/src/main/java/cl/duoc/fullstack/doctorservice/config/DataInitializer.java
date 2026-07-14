package cl.duoc.fullstack.doctorservice.config;

import cl.duoc.fullstack.doctorservice.model.Doctor;
import cl.duoc.fullstack.doctorservice.model.Specialty;
import cl.duoc.fullstack.doctorservice.repository.DoctorRepository;
import cl.duoc.fullstack.doctorservice.repository.SpecialtyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public void run(String... args) {
        ensureSpecialties();

        Map<String, Specialty> specialtiesByName = specialtyRepository.findAll().stream()
                .collect(HashMap::new, (acc, specialty) -> acc.put(specialty.getName(), specialty), HashMap::putAll);

        ensureDoctors(specialtiesByName);
        repairLegacyDoctors(specialtiesByName);
    }

    private void ensureSpecialties() {
        if (specialtyRepository.count() > 0) {
            return;
        }

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

    private void ensureDoctors(Map<String, Specialty> specialtiesByName) {
        if (doctorRepository.count() > 0) {
            return;
        }

        log.info("Inicializando doctores de ejemplo...");

        List<Doctor> doctors = List.of(
                buildDoctor("Carlos", "Méndez Rodríguez", "12.345.678-K", "carlos.mendez@clinica.cl", specialtiesByName.get("Cardiología")),
                buildDoctor("Alejandra", "González Silva", "13.456.789-1", "alejandra.gonzalez@clinica.cl", specialtiesByName.get("Medicina General")),
                buildDoctor("Roberto", "Fuentes Díaz", "14.567.890-2", "roberto.fuentes@clinica.cl", specialtiesByName.get("Neurología")),
                buildDoctor("Marcela", "López Martínez", "15.678.901-3", "marcela.lopez@clinica.cl", specialtiesByName.get("Pediatría")),
                buildDoctor("Javier", "Torres Acuña", "16.789.012-4", "javier.torres@clinica.cl", specialtiesByName.get("Traumatología")),
                buildDoctor("Sofía", "Valenzuela Flores", "17.890.123-5", "sofia.valenzuela@clinica.cl", specialtiesByName.get("Medicina General"))
        );

        doctorRepository.saveAll(doctors);
        log.info("✅ {} doctores creados", doctorRepository.count());
    }

    private void repairLegacyDoctors(Map<String, Specialty> specialtiesByName) {
        Map<String, LegacyDoctorSeed> legacySeedsByEmail = Map.of(
                "carlos@example.com", new LegacyDoctorSeed("Carlos", "Mendez Rodriguez", "12.345.678-K", "carlos.mendez@clinica.cl", "Cardiología"),
                "ana@example.com", new LegacyDoctorSeed("Ana", "Rodriguez", "18.901.234-6", "ana.rodriguez@clinica.cl", "Pediatría")
        );

        List<Doctor> incompleteDoctors = doctorRepository.findAll().stream()
                .filter(this::isIncomplete)
                .toList();

        if (incompleteDoctors.isEmpty()) {
            return;
        }

        int repaired = 0;
        for (Doctor doctor : incompleteDoctors) {
            LegacyDoctorSeed seed = legacySeedsByEmail.get(doctor.getEmail());
            if (seed == null) {
                log.warn("No se encontró semilla legacy para doctor id={} email={}", doctor.getId(), doctor.getEmail());
                continue;
            }

            boolean changed = false;
            if (isBlank(doctor.getFirstName())) {
                doctor.setFirstName(seed.firstName());
                changed = true;
            }
            if (isBlank(doctor.getLastName())) {
                doctor.setLastName(seed.lastName());
                changed = true;
            }
            if (isBlank(doctor.getNationalRegistry())) {
                doctor.setNationalRegistry(seed.nationalRegistry());
                changed = true;
            }
            if (isBlank(doctor.getEmail())) {
                doctor.setEmail(seed.email());
                changed = true;
            }
            if (doctor.getSpecialty() == null) {
                Specialty specialty = specialtiesByName.get(seed.specialtyName());
                if (specialty != null) {
                    doctor.setSpecialty(specialty);
                    changed = true;
                }
            }

            if (changed) {
                doctorRepository.save(doctor);
                repaired++;
            }
        }

        if (repaired > 0) {
            log.info("✅ {} doctores legacy incompletos fueron reparados", repaired);
        }
    }

    private boolean isIncomplete(Doctor doctor) {
        return isBlank(doctor.getFirstName())
                || isBlank(doctor.getLastName())
                || isBlank(doctor.getNationalRegistry())
                || doctor.getSpecialty() == null;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private Doctor buildDoctor(String firstName, String lastName, String nationalRegistry, String email, Specialty specialty) {
        return Doctor.builder()
                .firstName(firstName)
                .lastName(lastName)
                .nationalRegistry(nationalRegistry)
                .email(email)
                .specialty(specialty)
                .build();
    }

    private record LegacyDoctorSeed(String firstName, String lastName, String nationalRegistry, String email,
                                    String specialtyName) {
    }
}
