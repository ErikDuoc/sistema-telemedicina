# Payment Service

Microservicio Spring Boot para procesamiento de pagos médicos.

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
- Flyway
- Lombok
- Maven

## Construcción y pruebas

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar todos los tests
mvn test

# Ejecutar un test específico
mvn test -Dtest=PaymentServiceTest
mvn test -Dtest=PaymentControllerTest

# Empaquetar sin ejecutar tests
mvn clean package -DskipTests
```

Los tests unitarios cubren la capa de servicio (7 tests) y controlador (6 tests) usando JUnit 5, Mockito y AssertJ.

## Endpoints

### Procesar pago
POST /api/payments/process

### Listar seguros
GET /api/payments/insurances

### Obtener pago por ID
GET /api/payments/{id}

### Obtener todos los pagos
GET /api/payments

## Puerto

8084

## Consola H2

http://localhost:8084/h2-console

## Pruebas

| Capa | Archivo | Tests |
|------|---------|-------|
| Service | `PaymentServiceTest` | 7 |
| Controller | `PaymentControllerTest` | 6 |

**Patrones utilizados:**
- `@ExtendWith(MockitoExtension.class)` para pruebas de servicio con mocks
- `@WebMvcTest` + `@MockitoBean` para pruebas de controlador
- `@Import(SecurityConfig.class)` para habilitar la configuración de seguridad en tests
- Excepciones custom: `PaymentNotFoundException` → 404, `DuplicatePaymentException` → 409
