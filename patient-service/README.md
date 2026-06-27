# Patient Service

Microservicio Spring Boot para la gestión de pacientes en una plataforma de telemedicina.

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
mvn test -Dtest=PatientServiceTest
mvn test -Dtest=PatientControllerTest

# Empaquetar sin ejecutar tests
mvn clean package -DskipTests
```

Los tests unitarios cubren la capa de servicio (9 tests) y controlador (5 tests) usando JUnit 5, Mockito y AssertJ.

## Endpoints

### Obtener todos los pacientes
GET /api/patients

### Obtener paciente por ID
GET /api/patients/{id}

### Crear paciente
POST /api/patients

### Actualizar paciente
PUT /api/patients/{id}

### Eliminar paciente
DELETE /api/patients/{id}

## Puerto

8081

## Consola H2

http://localhost:8081/h2-console

## Pruebas

| Capa | Archivo | Tests |
|------|---------|-------|
| Service | `PatientServiceTest` | 9 |
| Controller | `PatientControllerTest` | 5 |

**Patrones utilizados:**
- `@ExtendWith(MockitoExtension.class)` para pruebas de servicio con mocks
- `@WebMvcTest` + `@MockBean` para pruebas de controlador
- `@Import(SecurityConfig.class)` para habilitar la configuración de seguridad en tests
- Excepciones custom: `ResourceNotFoundException` → 404, `DuplicateResourceException` → 409
