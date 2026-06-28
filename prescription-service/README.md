# Prescription Service

Microservicio Spring Boot para la gestión de recetas médicas en una plataforma de telemedicina.

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
mvn test -Dtest=PrescriptionServiceTest
mvn test -Dtest=PrescriptionControllerTest

# Empaquetar sin ejecutar tests
mvn clean package -DskipTests
```

Los tests unitarios cubren la capa de servicio (6 tests) y controlador (4 tests) usando JUnit 5, Mockito y AssertJ.

## Endpoints

### Crear receta
POST /api/prescriptions

### Obtener receta por ID
GET /api/prescriptions/{id}

## Puerto

8089

## Consola H2

http://localhost:8089/prescriptionservice/h2-console

## Pruebas

| Capa | Archivo | Tests |
|------|---------|-------|
| Service | `PrescriptionServiceTest` | 6 |
| Controller | `PrescriptionControllerTest` | 4 |

**Patrones utilizados:**
- `@ExtendWith(MockitoExtension.class)` para pruebas de servicio con mocks
- `@WebMvcTest` + `@MockitoBean` para pruebas de controlador
- `@Import(SecurityConfig.class)` para habilitar la configuración de seguridad en tests
- Excepciones custom: `PrescriptionNotFoundException` → 404, `DuplicatePrescriptionException` → 409
