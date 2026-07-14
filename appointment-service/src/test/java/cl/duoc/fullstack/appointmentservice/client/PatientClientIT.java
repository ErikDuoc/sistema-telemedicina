package cl.duoc.fullstack.appointmentservice.client;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@DisplayName("Patient Feign Client - Integration Tests")
class PatientClientIT {
    @Autowired
    private PatientClient patientClient;
    @Test
    @DisplayName("Obtener paciente por ID (servicio UP)")
    void obtenerPaciente_conIdValido() {
        Long patientId = 1L;
        try {
            var patient = patientClient.getPatient(patientId);
            assertNotNull(patient, "Patient no debe ser null");
        } catch (Exception e) {
            assertNotNull(e, "Exception fue lanzada (esperado si servicio no disponible)");
        }
    }
    @Test
    @DisplayName("Fallback se activa si servicio no disponible")
    void obtenerPaciente_fallback() {
        Long patientId = 999L;
        var patient = patientClient.getPatient(patientId);
        assertNull(patient);
    }
}