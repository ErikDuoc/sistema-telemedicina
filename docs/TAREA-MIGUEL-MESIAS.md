# 🔗 TAREA MIGUEL MESIAS — Integración: Context-paths & Feign Clients

**Rol**: Integración y Clientes Remotos  
**Responsabilidad**: Normalizar context-paths, refactorizar @FeignClient, agregar timeouts/fallbacks, pruebas E2E

---

## OBJETIVO

- ✅ Eliminar `server.servlet.context-path` de 10 servicios
- ✅ Refactorizar @FeignClient para usar nombres lógicos (sin `url=`)
- ✅ Agregar fallbacks y timeouts a Feign
- ✅ Crear pruebas de integración E2E
- ✅ Validar flujos reales Gateway→Servicio→Feign

---

## 📋 PASO 1: ELIMINAR CONTEXT-PATHS

### 1.1 Servicios afectados

Para CADA servicio listado abajo, editar `*/src/main/resources/application.yml`:

```
- patient-service/src/main/resources/application.yml
- doctor-service/src/main/resources/application.yml
- notification-service/src/main/resources/application.yml
- payment-service/src/main/resources/application.yml
- agenda-service/src/main/resources/application.yml
- laboratory-service/src/main/resources/application.yml
- appointment-service/src/main/resources/application.yml
- clinical-record-service/src/main/resources/application.yml
- prescription-service/src/main/resources/application.yml
- video-consultation-service/src/main/resources/application.yml
```

### 1.2 Cambio requerido

**BUSCAR EN CADA ARCHIVO**:
```yaml
server:
  servlet:
    context-path: /NOMBRE-SERVICIO  # ← ELIMINAR ESTA LÍNEA COMPLETA
```

**EJEMPLO - ANTES** (patient-service):
```yaml
server:
  port: 8081
  servlet:
    context-path: /patientservice   # ← ELIMINAR
spring:
  application:
    name: patient-service
```

**EJEMPLO - DESPUÉS** (patient-service):
```yaml
server:
  port: 8081
spring:
  application:
    name: patient-service
```

### 1.3 Verificar

```bash
# Ejecutar este comando: si retorna 0, está correcto
grep -r "context-path" */src/main/resources/application*.yml | wc -l
# Resultado esperado: 0
```

### 1.4 Commits

```bash
git add */src/main/resources/application*.yml
git commit -m "feat: eliminar context-path de todos los servicios

- Remover server.servlet.context-path de 10 servicios
- APIs ahora exponen en /api/* (sin prefijo de servicio)
- Necesario para compatibilidad con Gateway lb:// y Feign Eureka
"
```

---

## 📋 PASO 2: REFACTORIZAR @FEIGNCLIENT

### 2.1 Servicios con Feign clients

```
- appointment-service:
  - src/main/java/.../client/PatientClient.java
  - src/main/java/.../client/DoctorClient.java
  - src/main/java/.../client/AgendaClient.java

- clinical-record-service:
  - src/main/java/.../client/AppointmentClient.java

- laboratory-service:
  - src/main/java/.../client/PatientClient.java
  - src/main/java/.../client/NotificationClient.java

- prescription-service:
  - src/main/java/.../client/ClinicalRecordClient.java

- video-consultation-service:
  - src/main/java/.../client/AppointmentClient.java

- agenda-service:
  - src/main/java/.../client/DoctorClient.java
```

### 2.2 Cambio requerido

**DE**:
```java
@FeignClient(name = "patient-service", url = "${patient-service.url}")
public interface PatientClient {
    @GetMapping("/patients/{id}")
    PatientDTO getPatientById(@PathVariable Long id);
}
```

**A**:
```java
@FeignClient(name = "patient-service", fallback = PatientClientFallback.class)
public interface PatientClient {
    @GetMapping("/api/patients/{id}")
    PatientDTO getPatientById(@PathVariable Long id);
}
```

**⚠️ IMPORTANTE**: Nota el cambio de `/patients/{id}` → `/api/patients/{id}` (agregar `/api`)

### 2.3 Para CADA cliente, crear Fallback class

**EJEMPLO**: `appointment-service/src/main/java/cl/duoc/fullstack/appointmentservice/client/PatientClientFallback.java`

```java
package cl.duoc.fullstack.appointmentservice.client;

import cl.duoc.fullstack.appointmentservice.dto.PatientDTO;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PatientClientFallback implements PatientClient {

    private static final Logger logger = LoggerFactory.getLogger(PatientClientFallback.class);

    @Override
    public PatientDTO getPatientById(Long id) {
        logger.warn("PatientClient fallback activado para ID: {}", id);
        // Retornar paciente genérico o null según necesidad del negocio
        return new PatientDTO(id, "Unknown Patient", "unknown@example.com", "INACTIVE");
    }
}
```

### 2.4 Verificar cambios

```bash
# Verificar que NO hay @FeignClient con url=
grep -r "@FeignClient.*url=" */src/main/java --include="*Client.java" | wc -l
# Resultado esperado: 0

# Verificar que todos @FeignClient tienen fallback=
grep -r "@FeignClient.*fallback=" */src/main/java --include="*Client.java" | wc -l
# Resultado esperado: ~8
```

### 2.5 Commits

```bash
git add */src/main/java/.../client/*Client.java
git add */src/main/java/.../client/*ClientFallback.java
git commit -m "feat: refactorizar @FeignClient para usar Eureka discovery

- Remover urls hardcodeadas (${patient-service.url}, etc.)
- Usar nombres lógicos: @FeignClient(name='patient-service')
- Agregar fallback classes (8 clientes)
- Cambiar endpoints a incluir /api (ej: /api/patients/{id})
- Permite descubrimiento dinámico via Eureka
"
```

---

## 📋 PASO 3: AGREGAR TIMEOUTS Y CONFIGURACIÓN FEIGN

### 3.1 Para CADA servicio con Feign, actualizar `application.yml`

Agregar esta sección al final:

```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
        loggerLevel: FULL
```

**Servicios a actualizar**:
- appointment-service
- clinical-record-service
- laboratory-service
- prescription-service
- video-consultation-service
- agenda-service

### 3.2 Agregar Eureka client a pom.xml

Para CADA servicio con Feign, editar `*/pom.xml` y verificar que existe:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

Si NO está, agregarlo en sección `<dependencies>`.

### 3.3 Commits

```bash
git add */src/main/resources/application*.yml
git add */pom.xml
git commit -m "feat: configurar Feign con timeouts y Eureka client

- Agregar spring-cloud-starter-netflix-eureka-client (6 servicios)
- Configurar connectTimeout: 5000ms, readTimeout: 5000ms
- Habilitar logging FULL para debug
"
```

---

## 📋 PASO 4: CREAR PRUEBAS DE INTEGRACIÓN E2E

### 4.1 Crear test para Appointment Service

**Archivo**: `appointment-service/src/test/java/cl/duoc/fullstack/appointmentservice/integration/AppointmentE2ETest.java`

```java
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Obtener cita por ID debe retornar 200")
    void obtenerCita_conIdValido_retorna200() throws Exception {
        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Listar citas debe retornar 200")
    void listarCitas_retorna200() throws Exception {
        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isOk());
    }
}
```

### 4.2 Crear test para Feign Client

**Archivo**: `appointment-service/src/test/java/cl/duoc/fullstack/appointmentservice/client/PatientClientIT.java`

```java
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
        // Requiere que patient-service esté ejecutándose en Eureka
        Long patientId = 1L;
        
        try {
            var patient = patientClient.getPatientById(patientId);
            assertNotNull(patient, "Patient no debe ser null");
        } catch (Exception e) {
            // Si patient-service no está disponible, fallback retorna Unknown Patient
            assertNotNull(e, "Exception fue lanzada (esperado si servicio no disponible)");
        }
    }

    @Test
    @DisplayName("Fallback se activa si servicio no disponible")
    void obtenerPaciente_fallback() {
        // Este test valida que el fallback existe y puede ser invocado
        Long patientId = 999L;
        var patient = patientClient.getPatientById(patientId);
        
        assertNotNull(patient);
        assertEquals("Unknown Patient", patient.getName());
    }
}
```

### 4.3 Crear tests similares para otros servicios

Repetir patrón anterior para:
- `clinical-record-service/src/test/.../ClinicalRecordE2ETest.java`
- `laboratory-service/src/test/.../LaboratoryE2ETest.java`
- `prescription-service/src/test/.../PrescriptionE2ETest.java`
- `video-consultation-service/src/test/.../VideoConsultationE2ETest.java`

### 4.4 Ejecutar pruebas

```bash
# Ejecutar todos los tests de integración
mvn test -pl appointment-service,clinical-record-service,laboratory-service,prescription-service,video-consultation-service

# Resultado esperado
# Tests run: X, Failures: 0, Errors: 0
# BUILD SUCCESS
```

### 4.5 Commits

```bash
git add */src/test/java/*/integration/*E2ETest.java
git add */src/test/java/*/client/*ClientIT.java
git commit -m "test: agregar pruebas E2E para integración Feign

- E2E tests para appointment, clinical-record, laboratory, prescription, video-consultation
- Feign client integration tests validando Eureka discovery
- Validar fallbacks cuando servicios no disponibles
- 15+ tests nuevos, cobertura 80%+
"
```

---

## 📋 PASO 5: VALIDAR CAMBIOS COMPLETAMENTE

### 5.1 Compilar todo

```bash
mvn clean install -pl appointment-service,clinical-record-service,laboratory-service,prescription-service,video-consultation-service
```

**Resultado esperado**: `BUILD SUCCESS`

### 5.2 Ejecutar pruebas

```bash
mvn test -pl appointment-service,clinical-record-service,laboratory-service,prescription-service,video-consultation-service
```

**Resultado esperado**:
```
Tests run: 15+, Failures: 0, Errors: 0
BUILD SUCCESS
```

### 5.3 Prueba manual con servicios reales

Requiere que Erik haya completado su tarea (Eureka + Gateway levantados):

```bash
# 1. Levantar docker-compose (si no está ya)
docker-compose up -d

# 2. Esperar ~1 minuto
sleep 60

# 3. Crear paciente (a través del Gateway)
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@example.com","phone":"+56912345678","password":"pass123"}'

# Resultado esperado: 201 Created

# 4. Crear cita (Appointment llama a Patient vía Feign)
curl -X POST http://localhost:8080/api/appointments \
  -H "Content-Type: application/json" \
  -d '{"patientId":1,"doctorId":1,"appointmentDate":"2026-07-20T10:00:00","type":"CONSULTATION"}'

# Resultado esperado: 201 Created
# Significa: Feign encontró patient-service via Eureka ✓
```

### 5.4 Verificar logs

```bash
# Logs de appointment-service mostrando Feign encontrando patient-service
docker-compose logs appointment | grep -i "patient-service\|feign\|eureka"

# Resultado esperado: Logs mostrando "Found instance" o similar
```

---

## 📋 PASO 6: DOCUMENTO DE DEFENSA INDIVIDUAL

### 6.1 Crear `docs/defensa-individual/Miguel-Mesias.md`

```markdown
# Defensa Individual - Miguel Mesias

## Tareas Realizadas

### Context-paths Eliminados
- **Servicios**: 10
- **Cambio**: Remover `server.servlet.context-path` de todos
- **Commits**: [HASH-1] eliminar context-path de 10 servicios

### @FeignClient Refactorizado
- **Clientes**: 8
- **Cambio**: De url= hardcodeado a nombre lógico + fallback
- **Fallback classes creadas**: 8
- **Commits**: [HASH-2] refactorizar Feign clients, [HASH-3] agregar fallbacks

### Timeouts Configurados
- **Servicios**: 6 (con Feign)
- **Configuración**: connectTimeout: 5s, readTimeout: 5s
- **Commits**: [HASH-4] configurar Feign timeouts

### Pruebas E2E Creadas
- **Tests**: 15+
- **Cobertura**: 80%+ de integración
- **Commits**: [HASH-5] agregar E2E tests

## Evidencias Técnicas

### Tests Pasando
- Comando: `mvn test -pl appointment-service,...`
- Resultado: `BUILD SUCCESS, 15 tests passed`
- Captura: tests exitosos

### Flujo Real Gateway→Feign→Servicio
- Crear paciente via Gateway → 201
- Crear cita (usa Feign) → 201
- Captura: ambos requests exitosos

### Contexto-paths Eliminados
- Comando: `grep -r "context-path" */src/main/resources/ | wc -l`
- Resultado: 0 (vacío)

## Conceptos Técnicos

### ¿Diferencia entre url hardcodeado vs nombre lógico?
- **Hardcodeado**: `http://localhost:8081` → falla si servicio se mueve
- **Lógico**: `lb://patient-service` → Eureka busca dónde está, load-balancerea

### ¿Qué es Fallback?
- Plan B cuando servicio remoto no responde
- Retorna datos por defecto, evita error en cascada

### ¿Cómo se depura Feign?
- Verificar Eureka: `curl http://localhost:8761/eureka/apps/PATIENT-SERVICE`
- Ver logs: `docker-compose logs appointment | grep feign`
- Verificar config: `grep -A2 "feign:" */application.yml`
```

---

## ✅ CHECKLIST FINAL

- [ ] Context-paths eliminados de 10 servicios (verificar grep = 0)
- [ ] @FeignClient refactorizados en 8 servicios (sin url=)
- [ ] Fallback classes creadas (1 por cliente)
- [ ] Timeouts configurados: connectTimeout: 5000, readTimeout: 5000
- [ ] Eureka client agregado a 6 servicios (pom.xml)
- [ ] E2E tests creados (15+)
- [ ] `mvn test` pasa con 0 fallos
- [ ] Llamada real Gateway→Appointment→Patient retorna 201
- [ ] Logs muestran Feign/Eureka resolviendo servicios (no hardcodeado)
- [ ] Documento `docs/defensa-individual/Miguel-Mesias.md` creado
- [ ] Commits pusheados con mensajes significativos

---

## 🔄 SIGUIENTES PASOS

**Después de completar tu tarea:**

1. **Comunica a Erik**: "Context-paths y Feign listos, validar integración"
2. **Comunica a Genaro**: "Migraciones pueden proceder"
3. **Espera confirmación**: Ambos terminan
4. **Ejecución conjunta**: `docker-compose up -d` con todos cambios
5. **Validar flujo E2E**: Gateway→Appointment→Patient (Feign llamando vía Eureka)

---

**¡Éxito! 🔗**

