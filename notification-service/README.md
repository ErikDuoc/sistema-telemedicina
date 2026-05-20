# Notification Service

Microservicio Spring Boot para el envío y registro de notificaciones.

## Tecnologías

- Java 21
- Spring Boot 3.5
- Spring Data JPA
- H2 Database
- Maven

## Endpoints

### Enviar notificación
POST /api/notifications/send

### Historial de notificaciones
GET /api/notifications/history/{userId}

## Puerto

8083

## Consola H2

http://localhost:8083/h2-console
