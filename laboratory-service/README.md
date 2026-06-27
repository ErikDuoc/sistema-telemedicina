# Laboratory Service

Microservicio Spring Boot para la gestión de órdenes de laboratorio y resultados de exámenes.

## Requisitos

- Java 21
- Maven 3.8+

## Tecnologías

- Java 21
- Spring Boot 3.4
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
mvn test -Dtest=LaboratoryServiceTest
mvn test -Dtest=LaboratoryControllerTest

# Empaquetar sin ejecutar tests
mvn clean package -DskipTests
```

Los tests unitarios cubren la capa de servicio (8 tests) y controlador (4 tests) usando JUnit 5, Mockito y AssertJ.

## Endpoints

### Crear orden de laboratorio
POST /api/lab/orders

### Cargar resultado de examen
PUT /api/lab/results/{orderId}

### Obtener órdenes del paciente
GET /api/lab/patient/{id}

## Puerto

8086

Requiere patient-service ejecutándose en 8081 y notification-service en 8083.

## Pruebas

| Capa | Archivo | Tests |
|------|---------|-------|
| Service | `LaboratoryServiceTest` | 8 |
| Controller | `LaboratoryControllerTest` | 4 |

**Patrones utilizados:**
- `@ExtendWith(MockitoExtension.class)` para pruebas de servicio con mocks
- `@WebMvcTest` + `@MockBean` para pruebas de controlador
- `@Import(SecurityConfig.class)` para habilitar la configuración de seguridad en tests
- Excepciones custom: `LaboratoryNotFoundException` → 404, `DuplicateLaboratoryException` → 409
