# Doctor Service

Microservicio Spring Boot para la gestión de médicos y especialidades.

## Tecnologías

- Java 21
- Spring Boot 3.5
- Spring Data JPA
- H2 Database
- Maven

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