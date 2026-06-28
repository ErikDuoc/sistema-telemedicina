# 🏥 Telemedicina System

[![Java](https://img.shields.io/badge/Java-21-%23ED8B00)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-%236DB33F)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-%236DB33F)](https://spring.io/projects/spring-cloud)
[![Tests](https://img.shields.io/badge/Tests-129_passing-%2328A745)](http://localhost)
[![License](https://img.shields.io/badge/License-MIT-%23FF5722)]()

## 👥 Estudiantes

- **Miguel Mesías**
- **Genaro Lagos**
- **Erik Queirolo**

Plataforma de telemedicina modular basada en microservicios con Spring Boot. Permite gestionar pacientes, médicos, citas, recetas, pagos, videoconsultas y más, todo integrado bajo una arquitectura desacoplada y testeable.

---

## 📋 Tabla de Contenidos

- [Arquitectura](#arquitectura)
- [Stack Tecnológico](#stack-tecnológico)
- [Prerrequisitos](#prerrequisitos)
- [Inicio Rápido](#inicio-rápido)
- [Servicios](#servicios)
- [Testing](#testing)
- [Base de Datos](#base-de-datos)
- [Docker](#docker)
- [Patrones Comunes](#patrones-comunes)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Documentación Adicional](#documentación-adicional)

---

## 🏛️ Arquitectura

```
                   ┌─────────────────────────┐
                   │    Cliente (Web/Móvil)   │
                   └────────────┬────────────┘
                                │ HTTP
                                ▼
                   ┌─────────────────────────┐
                   │   API Gateway (:8080)    │
                   │   Spring Cloud Gateway   │
                   └──┬──┬──┬──┬──┬──┬──┬──┬──┘
                      │  │  │  │  │  │  │  │
                      ▼  ▼  ▼  ▼  ▼  ▼  ▼  ▼
            ┌───────────────────────────────────┐
            │        Microservicios REST        │
            │   (cada uno en su propio puerto)  │
            └───────────────────────────────────┘

  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
  │Patient │ │Doctor  │ │Agenda  │ │Appoint.│ │Clinical│
  │:8081   │ │:8082   │ │:8085   │ │:8087   │ │:8088   │
  └────────┘ └────────┘ └────────┘ └────────┘ └────────┘

  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
  │Payment │ │Notifica│ │Laborat.│ │Prescrip│ │Video   │
  │:8084   │ │:8083   │ │:8086   │ │:8089   │ │:8091   │
  └────────┘ └────────┘ └────────┘ └────────┘ └────────┘
```

Cada microservicio es independiente, con su propia base de datos y ciclo de vida. Se comunican vía Feign Client y están documentados con OpenAPI/Swagger. El **API Gateway** (Spring Cloud Gateway) actúa como punto de entrada único en el puerto 8080, enrutando las peticiones al microservicio correspondiente.

---

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Uso |
|-----------|---------|-----|
| Java | 21 | JDK |
| Spring Boot | 3.5.0 | Framework principal |
| Spring Cloud | 2025.0.0 | Feign Clients, discovery |
| Spring Data JPA | — | Persistencia ORM |
| Spring HATEOAS | — | APIs hipermedia |
| Spring Validation | — | Validación de entrada |
| Spring Security | — | Seguridad |
| H2 Database | — | Base de datos embebida (dev) |
| MySQL | 8.0+ | Base de datos (producción) |
| Lombok | — | Reducción de boilerplate |
| JJWT | 0.11.5 | JWT |
| SpringDoc OpenAPI | 2.8.9 | Documentación Swagger |
| Flyway | — | Migraciones de BD |
| Maven | 3.8+ | Build |

---

## 📦 Prerrequisitos

```bash
java -version    # Java 21+
mvn -version     # Maven 3.8+
```

---

## 🚀 Inicio Rápido

```bash
# 1. Compilar todo el proyecto
mvn clean install

# 2. Ejecutar tests (opcional)
mvn test

# 3. Iniciar el API Gateway
mvn spring-boot:run -pl gateway-service

# 4. Iniciar un servicio (ej: patient-service)
mvn spring-boot:run -pl patient-service

# 5. Acceder a los servicios vía Gateway
# http://localhost:8080/api/patients
# http://localhost:8080/swagger-ui.html (cuando se configure)
```

Para desarrollo local se usa H2 en memoria (perfil por defecto). No requiere instalación de base de datos externa.

---

## 🧩 Servicios

| # | Servicio | Puerto | Descripción | CRUD | Tests |
|---|----------|--------|-------------|------|-------|
| — | **gateway-service** | 8080 | API Gateway (Spring Cloud Gateway) | — | — |
| 1 | **patient-service** | 8081 | Gestión de pacientes | Sí | 19 |
| 2 | **doctor-service** | 8082 | Catálogo de médicos | Sí | 11 |
| 3 | **notification-service** | 8083 | Notificaciones (email/SMS) | Sí | 13 |
| 4 | **payment-service** | 8084 | Procesamiento de pagos | Parcial | 14 |
| 5 | **agenda-service** | 8085 | Disponibilidad de médicos | Sí | 11 |
| 6 | **laboratory-service** | 8086 | Órdenes y resultados de laboratorio | Sí | 12 |
| 7 | **appointment-service** | 8087 | Reserva y gestión de citas | Sí | 16 |
| 8 | **clinical-record-service** | 8088 | Ficha clínica digital | Sí | 12 |
| 9 | **prescription-service** | 8089 | Recetas electrónicas | Parcial | 10 |
| 10 | **video-consultation-service** | 8091 | Videoconsultas | Parcial | 11 |

### Endpoints Comunes

Cada servicio expone endpoints REST siguiendo el patrón `/api/{recurso}`:

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/{recurso}` | Listar todos |
| `GET` | `/api/{recurso}/{id}` | Obtener por ID |
| `POST` | `/api/{recurso}` | Crear |
| `PUT` | `/api/{recurso}/{id}` | Actualizar |
| `DELETE` | `/api/{recurso}/{id}` | Eliminar |

Los endpoints que retornan listas o elementos individuales incluyen enlaces **HATEOAS** en `_links` para descubrimiento de API.

---

### 🌐 API Gateway — Rutas

Todas las peticiones pasan por el **API Gateway** (`http://localhost:8080`), que enruta automáticamente a cada servicio:

| Ruta | Destino | Puerto |
|------|---------|--------|
| `/api/patients/**` | patient-service | 8081 |
| `/api/doctors/**` | doctor-service | 8082 |
| `/api/notifications/**` | notification-service | 8083 |
| `/api/payments/**` | payment-service | 8084 |
| `/api/agenda/**` | agenda-service | 8085 |
| `/api/lab/**` | laboratory-service | 8086 |
| `/api/appointments/**` | appointment-service | 8087 |
| `/api/clinical-records/**` | clinical-record-service | 8088 |
| `/api/prescriptions/**` | prescription-service | 8089 |
| `/api/video-consultations/**` | video-consultation-service | 8091 |

---

### 📖 Documentación Swagger

Cada servicio expone su propia documentación Swagger:

| Servicio | URL Swagger |
|----------|-------------|
| patient-service | `http://localhost:8081/swagger-ui.html` |
| doctor-service | `http://localhost:8082/swagger-ui.html` |
| notification-service | `http://localhost:8083/swagger-ui.html` |
| payment-service | `http://localhost:8084/swagger-ui.html` |
| agenda-service | `http://localhost:8085/swagger-ui.html` |
| laboratory-service | `http://localhost:8086/swagger-ui.html` |
| appointment-service | `http://localhost:8087/swagger-ui.html` |
| clinical-record-service | `http://localhost:8088/swagger-ui.html` |
| prescription-service | `http://localhost:8089/swagger-ui.html` |
| video-consultation-service | `http://localhost:8091/swagger-ui.html` |

---

## 🧪 Testing

```bash
# Ejecutar todos los tests del proyecto
mvn test

# Ejecutar tests de un servicio específico
mvn test -pl patient-service

# Ejecutar un test específico
mvn test -pl patient-service -Dtest=PatientServiceTest
```

### Resumen de tests

| Servicio | Service | Controller | Total |
|----------|---------|------------|-------|
| patient-service | 9 | 10 | 19 |
| doctor-service | 6 | 5 | 11 |
| notification-service | 7 | 6 | 13 |
| payment-service | 7 | 7 | 14 |
| agenda-service | 6 | 5 | 11 |
| laboratory-service | 8 | 4 | 12 |
| appointment-service | 10 | 6 | 16 |
| clinical-record-service | 7 | 5 | 12 |
| prescription-service | 6 | 4 | 10 |
| video-consultation-service | 6 | 5 | 11 |
| **Total** | **72** | **57** | **129** |

Todos los tests usan JUnit 5 + Mockito. Los tests de servicio usan `@ExtendWith(MockitoExtension.class)`. Los tests de controlador usan `@WebMvcTest` + `@Import(SecurityConfig.class)`.

---

## 🗄️ Base de Datos

Cada servicio tiene su propia base de datos independiente.

### Desarrollo (perfil por defecto)

```yaml
spring.datasource.url: jdbc:h2:mem:servicename_db
spring.jpa.hibernate.ddl-auto: update
```

Consola H2 disponible en `http://localhost:{puerto}/h2-console`

### Producción (perfil `mysql`)

```yaml
spring.datasource.url: jdbc:mysql://localhost:3306/servicename_db
spring.jpa.hibernate.ddl-auto: update
spring.profiles.active: mysql
```

---

## 🐳 Docker

```bash
# Iniciar MySQL + servicios
docker-compose up -d
```

El `docker-compose.yml` incluye MySQL 8.0, gateway-service y los siguientes servicios: doctor, agenda, appointment, patient, payment, clinical-record, notification, prescription, video-consultation, laboratory.

---

## 🔄 Patrones Comunes

### Manejo de Excepciones

Cada servicio implementa un `GlobalExceptionHandler` con `@RestControllerAdvice` que captura:

| Excepción | Código | Descripción |
|-----------|--------|-------------|
| `*NotFoundException` | 404 | Recurso no encontrado |
| `Duplicate*Exception` | 409 | Recurso duplicado |
| `MethodArgumentNotValidException` | 400 | Error de validación |
| `IllegalArgumentException` | 400 | Argumento inválido |
| `Exception` | 500 | Error interno |

### HATEOAS

Los endpoints GET retornan modelos hipermedia (`EntityModel`/`CollectionModel`) con enlaces en `_links` para permitir descubrimiento de API desde el cliente.

### Feign Clients

Los servicios se comunican entre sí mediante Feign Clients para llamadas síncronas (ej: prescription-service consulta clinical-record-service).

---

## 📁 Estructura del Proyecto

```
telemedicina-system/
├── pom.xml                        # Maven parent POM
├── README.md                      # Este archivo
├── docker-compose.yml             # MySQL + servicios
├── init-db.sql                    # Inicialización de bases de datos
│
├── gateway-service/               # :8080 (API Gateway)
├── patient-service/               # :8081
├── doctor-service/                # :8082
├── notification-service/          # :8083
├── payment-service/               # :8084
├── agenda-service/                # :8085
├── laboratory-service/            # :8086
├── appointment-service/           # :8087
├── clinical-record-service/       # :8088
├── prescription-service/          # :8089
└── video-consultation-service/    # :8091
```

Cada servicio sigue la misma estructura interna:

```
service-name/
├── pom.xml
├── Dockerfile
├── README.md
└── src/
    ├── main/java/cl/duoc/fullstack/servicename/
    │   ├── *Application.java
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── model/
    │   ├── dto/
    │   ├── exception/
    │   ├── config/
    │   ├── filter/
    │   └── client/
    └── main/resources/
        ├── application.yml
        └── application-mysql.yml
```

---

## 📚 Documentación Adicional

| Archivo | Descripción |
|---------|-------------|
| `ARQUITECTURA_MEJORADA.md` | Documentación técnica detallada de la arquitectura |
| `DEPLOYMENT.md` | Guía de despliegue a producción |
| `SEGURIDAD.md` | Configuración de seguridad y JWT |
| `HATEOAS_DOCUMENTATION.md` | Documentación del patrón HATEOAS |
| `docker-compose.yml` | Stack Docker para desarrollo |
| `init-production.sh` | Script de inicialización para producción |

---

## 📄 Licencia

MIT
