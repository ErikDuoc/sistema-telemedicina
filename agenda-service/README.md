# Agenda Service

Microservicio Spring Boot para la gestión de agendas y disponibilidad de médicos.

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
mvn test -Dtest=AgendaServiceTest
mvn test -Dtest=AgendaControllerTest

# Empaquetar sin ejecutar tests
mvn clean package -DskipTests
```

Los tests unitarios cubren la capa de servicio (6 tests) y controlador (5 tests) usando JUnit 5, Mockito y AssertJ.

## Endpoints

### Configurar disponibilidad
POST /api/agenda/setup

### Obtener agenda del médico
GET /api/agenda/doctor/{id}

## Puerto

8085

Requiere doctor-service ejecutándose en 8082.

## Pruebas

| Capa | Archivo | Tests |
|------|---------|-------|
| Service | `AgendaServiceTest` | 6 |
| Controller | `AgendaControllerTest` | 5 |

**Patrones utilizados:**
- `@ExtendWith(MockitoExtension.class)` para pruebas de servicio con mocks
- `@WebMvcTest` + `@MockBean` para pruebas de controlador
- `@Import(SecurityConfig.class)` para habilitar la configuración de seguridad en tests
- Excepciones custom: `AgendaNotFoundException` → 404, `DuplicateAgendaException` → 409
