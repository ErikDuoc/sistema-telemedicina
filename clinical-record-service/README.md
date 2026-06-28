# Clinical Record Service

Microservicio Spring Boot para la gestión de fichas clínicas asociadas a citas médicas.

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
mvn test -Dtest=ClinicalRecordServiceTest
mvn test -Dtest=ClinicalRecordControllerTest

# Empaquetar sin ejecutar tests
mvn clean package -DskipTests
```

Los tests unitarios cubren la capa de servicio (7 tests) y controlador (4 tests) usando JUnit 5, Mockito y AssertJ.

## Endpoints

### Crear ficha clínica
POST /api/clinical-records

### Obtener ficha clínica por ID
GET /api/clinical-records/{id}

## Puerto

8084

Requiere appointment-service ejecutándose en 8086.

## Pruebas

| Capa | Archivo | Tests |
|------|---------|-------|
| Service | `ClinicalRecordServiceTest` | 7 |
| Controller | `ClinicalRecordControllerTest` | 4 |

**Patrones utilizados:**
- `@ExtendWith(MockitoExtension.class)` para pruebas de servicio con mocks
- `@WebMvcTest` + `@MockBean` para pruebas de controlador
- `@Import(SecurityConfig.class)` para habilitar la configuración de seguridad en tests
- Excepciones custom: `ClinicalRecordNotFoundException` → 404, `DuplicateClinicalRecordException` → 409
