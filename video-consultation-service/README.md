# Video Consultation Service

Microservicio Spring Boot para la gestión de consultas médicas por videollamada en una plataforma de telemedicina.

## Requisitos

- Java 21
- Maven 3.8+

## Tecnologías

- Java 21
- Spring Boot 3.5
- Spring Data JPA
- H2 Database
- Spring Cloud OpenFeign
- HATEOAS
- Lombok
- Maven

## Construcción y pruebas

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar todos los tests
mvn test

# Ejecutar un test específico
mvn test -Dtest=VideoConsultationServiceTest
mvn test -Dtest=VideoConsultationControllerTest

# Empaquetar sin ejecutar tests
mvn clean package -DskipTests
```

Los tests unitarios cubren la capa de servicio (6 tests) y controlador (4 tests) usando JUnit 5, Mockito y AssertJ.

## Endpoints

### Crear videoconsulta
POST /api/video-consultations

### Obtener videoconsulta por ID
GET /api/video-consultations/{id}

## Puerto

8091

## Consola H2

http://localhost:8091/videoconsultationservice/h2-console

## Pruebas

| Capa | Archivo | Tests |
|------|---------|-------|
| Service | `VideoConsultationServiceTest` | 6 |
| Controller | `VideoConsultationControllerTest` | 4 |

**Patrones utilizados:**
- `@ExtendWith(MockitoExtension.class)` para pruebas de servicio con mocks
- `@WebMvcTest` + `@MockitoBean` para pruebas de controlador
- `@Import(SecurityConfig.class)` para habilitar la configuración de seguridad en tests
- Excepciones custom: `VideoConsultationNotFoundException` → 404, `DuplicateVideoConsultationException` → 409
