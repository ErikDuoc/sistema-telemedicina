# Notification Service

Microservicio Spring Boot para el envío y registro de notificaciones.

## Requisitos

- Java 21
- Maven 3.8+

## Tecnologías

- Java 21
- Spring Boot 3.5
- Spring Data JPA
- H2 Database
- Maven

## Construcción y pruebas

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar todos los tests
mvn test

# Ejecutar un test específico
mvn test -Dtest=NotificationServiceTest
mvn test -Dtest=NotificationControllerTest

# Empaquetar sin ejecutar tests
mvn clean package -DskipTests
```

Los tests unitarios cubren la capa de servicio (7 tests) y controlador (4 tests) usando JUnit 5, Mockito y AssertJ.

## Endpoints

### Enviar notificación
POST /api/notifications/send

### Obtener notificación por ID
GET /api/notifications/{id}

### Historial de notificaciones
GET /api/notifications/history/{userId}

## Puerto

8083

## Consola H2

http://localhost:8083/h2-console

## Pruebas

| Capa | Archivo | Tests |
|------|---------|-------|
| Service | `NotificationServiceTest` | 7 |
| Controller | `NotificationControllerTest` | 4 |

**Patrones utilizados:**
- `@ExtendWith(MockitoExtension.class)` para pruebas de servicio con mocks
- `@WebMvcTest` + `@MockBean` para pruebas de controlador
- `@Import(SecurityConfig.class)` para habilitar la configuración de seguridad en tests
- Excepciones custom: `NotificationNotFoundException` → 404, `DuplicateNotificationException` → 409
