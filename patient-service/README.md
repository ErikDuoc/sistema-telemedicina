# Patient Service - Clínica Virtual

Microservicio desarrollado con Spring Boot para la gestión de pacientes
en una plataforma de telemedicina y gestión hospitalaria.

## Tecnologías

- Java 21
- Spring Boot 3.5
- Spring Data JPA
- H2 Database
- Spring Cloud OpenFeign (preparado para integración futura)
- Lombok
- Maven

## Arquitectura

El proyecto utiliza arquitectura por capas:

- controller
- service
- repository
- dto
- model
- config

## Funcionalidades

- Registrar pacientes
- Listar pacientes
- Actualizar pacientes
- Eliminar pacientes
- Gestión de contactos de emergencia
- Validaciones con Jakarta Validation
- Manejo global de excepciones
- Datos de prueba automáticos

## Endpoints

### Obtener pacientes
GET /api/patients

### Obtener paciente por ID
GET /api/patients/{id}

### Crear paciente
POST /api/patients

### Actualizar paciente
PUT /api/patients/{id}

### Eliminar paciente
DELETE /api/patients/{id}

## Consola H2

http://localhost:8081/h2-console

JDBC URL:
jdbc:h2:mem:patientsdb