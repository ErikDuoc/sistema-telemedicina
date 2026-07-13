package cl.duoc.fullstack.appointmentservice.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Appointment Service - E2E Tests")
class AppointmentE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Crear cita con paciente válido debe retornar 201")
    void crearCita_conPacienteValido_retorna201() throws Exception {
        String requestBody = """
            {
              "patientId": 1,
              "doctorId": 1,
              "appointmentDate": "2026-07-20T10:00:00",
              "type": "CONSULTATION"
            }
            """;

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());
    }
}
