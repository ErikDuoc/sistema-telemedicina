# Appointment Service

Microservicio Spring Boot para la gestión de citas médicas.

## Requisitos

- Java 21
- Maven 3.8+

## Tecnologías

- Java 21
- Spring Boot 3.5
- Spring Data JPA
- H2 Database
- OpenFeign
- HATEOAS
- Maven

## Construcción y pruebas

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar todos los tests
mvn test

# Ejecutar un test específico
mvn test -Dtest=AppointmentServiceTest
mvn test -Dtest=AppointmentControllerTest

# Empaquetar sin ejecutar tests
mvn clean package -DskipTests
```

Los tests unitarios cubren la capa de servicio (10 tests) y controlador (6 tests) usando JUnit 5, Mockito y AssertJ.

## Endpoints

### Crear cita médica
POST /api/appointments

### Obtener citas del paciente
GET /api/appointments/patient/{id}

### Actualizar estado de cita
PATCH /api/appointments/{id}/status?status={status}

Estados válidos: PENDING, CONFIRMED, CANCELLED, COMPLETED

## Puerto

8087

Requiere doctor-service (8082), agenda-service (8085) y patient-service (8081) ejecutándose.

## Pruebas

| Capa | Archivo | Tests |
|------|---------|-------|
| Service | `AppointmentServiceTest` | 10 |
| Controller | `AppointmentControllerTest` | 6 |

**Patrones utilizados:**
- `@ExtendWith(MockitoExtension.class)` para pruebas de servicio con mocks
- `@WebMvcTest` + `@MockBean` para pruebas de controlador
- `@Import(SecurityConfig.class)` para habilitar la configuración de seguridad en tests
- Excepciones custom: `AppointmentNotFoundException` → 404, `DuplicateAppointmentException` → 409
