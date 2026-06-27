# Doctor Service

Microservicio Spring Boot para la gestión de médicos y especialidades.

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
mvn test -Dtest=DoctorServiceTest
mvn test -Dtest=DoctorControllerTest

# Empaquetar sin ejecutar tests
mvn clean package -DskipTests
```

Los tests unitarios cubren la capa de servicio (6 tests) y controlador (5 tests) usando JUnit 5, Mockito y AssertJ.

## Endpoints

### Crear médico
POST /api/doctors

### Listar médicos
GET /api/doctors

### Obtener perfil médico
GET /api/doctors/{id}/profile

## Puerto

8082

## Consola H2

http://localhost:8082/h2-console

## Integración Docker
Para utilizar MySQL:
1.- Creamos archivo compose.yml
2.- Configuramos pom.xml con flyway MySQL y application-mysql.yml con la información de la BD configurada en compose.yml
3.- Tener abierto docker desktop
4.- Desde consola, sobre la carpeta del microservicio utilizamos docker compose up -d
5.- Se puede revisar la base de datos desde Intellij configurando su puerto y contraseñas

## Pruebas

| Capa | Archivo | Tests |
|------|---------|-------|
| Service | `DoctorServiceTest` | 6 |
| Controller | `DoctorControllerTest` | 5 |

**Patrones utilizados:**
- `@ExtendWith(MockitoExtension.class)` para pruebas de servicio con mocks
- `@WebMvcTest` + `@MockBean` para pruebas de controlador
- `@Import(SecurityConfig.class)` para habilitar la configuración de seguridad en tests
- Excepciones custom: `DoctorNotFoundException` → 404, `DuplicateDoctorException` → 409, `SpecialtyNotFoundException` → 400