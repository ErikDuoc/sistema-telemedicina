package cl.duoc.fullstack.patientservice.config;

import cl.duoc.fullstack.patientservice.model.EmergencyContact;
import cl.duoc.fullstack.patientservice.model.Patient;
import cl.duoc.fullstack.patientservice.model.User;
import cl.duoc.fullstack.patientservice.model.UserRole;
import cl.duoc.fullstack.patientservice.repository.PatientRepository;
import cl.duoc.fullstack.patientservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile("h2")
    CommandLineRunner initUsers() {
        return args -> {
            if (userRepository.count() == 0) {
                log.info("Inicializando usuarios de ejemplo...");

                User admin = new User();
                admin.setName("Administrador");
                admin.setEmail("admin@telemedicina.cl");
                admin.setPassword(passwordEncoder.encode("pass123"));
                admin.setRole(UserRole.ADMIN);
                admin.setActive(true);
                userRepository.save(admin);

                User doctor = new User();
                doctor.setName("Dr. Roberto Silva");
                doctor.setEmail("doctor@telemedicina.cl");
                doctor.setPassword(passwordEncoder.encode("user123"));
                doctor.setRole(UserRole.DOCTOR);
                doctor.setActive(true);
                userRepository.save(doctor);

                User patient = new User();
                patient.setName("Ana Garcia");
                patient.setEmail("paciente@telemedicina.cl");
                patient.setPassword(passwordEncoder.encode("user123"));
                patient.setRole(UserRole.PATIENT);
                patient.setActive(true);
                userRepository.save(patient);

                log.info("✅ {} usuarios creados exitosamente", userRepository.count());
            }
        };
    }

    @Bean
    @Profile("h2")
    CommandLineRunner loadData() {
        return args -> {

            if (patientRepository.count() > 0) {
                log.info("Base de datos de pacientes ya inicializada");
                return;
            }

            log.info("Inicializando 10 pacientes de ejemplo...");

            List<Patient> patients = List.of(
                createPatient("12.345.678-9", "Juan", "Pérez García", LocalDate.of(1985, 5, 15), "Masculino", "juan.perez@email.com", "Fonasa A", "María Pérez", "Hermana", "+56912345678"),
                createPatient("13.456.789-0", "María", "González López", LocalDate.of(1990, 8, 22), "Femenino", "maria.gonzalez@email.com", "Fonasa B", "Carlos González", "Padre", "+56987654321"),
                createPatient("14.567.890-1", "Carlos", "Rodríguez Martínez", LocalDate.of(1978, 3, 10), "Masculino", "carlos.rodriguez@email.com", "Isapre Vida", "Ana Rodríguez", "Esposa", "+56912398765"),
                createPatient("15.678.901-2", "Sandra", "Fernández Díaz", LocalDate.of(1995, 12, 5), "Femenino", "sandra.fernandez@email.com", "Fonasa C", "Roberto Fernández", "Hermano", "+56912567890"),
                createPatient("16.789.012-3", "Roberto", "Silva Acuña", LocalDate.of(1988, 7, 18), "Masculino", "roberto.silva@email.com", "Isapre Masvida", "Patricia Silva", "Madre", "+56923456789"),
                createPatient("17.890.123-4", "Lucía", "Campos Rojas", LocalDate.of(1992, 11, 30), "Femenino", "lucia.campos@email.com", "Fonasa D", "Javier Campos", "Padre", "+56934567890"),
                createPatient("18.901.234-5", "Francisco", "Valenzuela Moreno", LocalDate.of(1980, 2, 14), "Masculino", "francisco.valenzuela@email.com", "Banmédica", "Gloria Valenzuela", "Esposa", "+56945678901"),
                createPatient("19.012.345-6", "Verónica", "Muñoz Flores", LocalDate.of(1987, 9, 25), "Femenino", "veronica.munoz@email.com", "Consalud", "David Muñoz", "Hermano", "+56956789012"),
                createPatient("20.123.456-7", "Andrés", "Gutiérrez Soto", LocalDate.of(1993, 6, 8), "Masculino", "andres.gutierrez@email.com", "Fonasa A", "Elena Gutiérrez", "Madre", "+56967890123"),
                createPatient("21.234.567-8", "Isabel", "Cortés Núñez", LocalDate.of(1989, 4, 20), "Femenino", "isabel.cortes@email.com", "Isapre Integra", "Rodrigo Cortés", "Esposo", "+56978901234")
            );

            patientRepository.saveAll(patients);
            log.info("✅ {} pacientes creados exitosamente", patientRepository.count());
        };
    }

    private Patient createPatient(String rut, String nombre, String apellido, LocalDate fechaNac,
                                   String genero, String email, String prevision,
                                   String contactNombre, String parentesco, String telefono) {
        Patient patient = Patient.builder()
                .rut(rut)
                .nombre(nombre)
                .apellido(apellido)
                .fechaNacimiento(fechaNac)
                .genero(genero)
                .email(email)
                .prevision(prevision)
                .build();

        EmergencyContact contact = EmergencyContact.builder()
                .nombre(contactNombre)
                .parentesco(parentesco)
                .telefono(telefono)
                .patient(patient)
                .build();

        patient.setContactosEmergencia(List.of(contact));
        return patient;
    }
}
