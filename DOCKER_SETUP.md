# Docker Setup - Microservicios

Este documento explica cómo ejecutar los microservicios con Docker Compose.

## Archivos Generados

Se han creado los siguientes archivos para la integración con Docker:

### Por Microservicio

#### doctor-service
- **`doctor-service/Dockerfile`**: Multi-stage Dockerfile (Maven + Temurin 21)
- **`doctor-service/.dockerignore`**: Excluye archivos innecesarios

#### agenda-service
- **`agenda-service/Dockerfile`**: Multi-stage Dockerfile (Maven + Temurin 21)
- **`agenda-service/.dockerignore`**: Excluye archivos innecesarios
- **`agenda-service/src/main/resources/application-mysql.yml`**: Configuración MySQL para la BD `agenda_db`
- **`agenda-service/pom.xml`**: Añadida dependencia `flyway-mysql` (v10.11.0)

#### appointment-service
- **`appointment-service/Dockerfile`**: Multi-stage Dockerfile (Maven + Temurin 21)
- **`appointment-service/.dockerignore`**: Excluye archivos innecesarios
- **`appointment-service/src/main/resources/application-mysql.yml`**: Configuración MySQL para la BD `appointment_db`
- **`appointment-service/src/main/java/.../client/*Client.java`**: URLs de Feign clients externalizadas via properties

#### patient-service
- **`patient-service/Dockerfile`**: Multi-stage Dockerfile (Maven + Temurin 21)
- **`patient-service/.dockerignore`**: Excluye archivos innecesarios
- **`patient-service/src/main/resources/application-mysql.yml`**: Configuración MySQL actualizada para la BD `patients_db` con usuario dedicado
- **`patient-service/pom.xml`**: Añadida dependencia `flyway-mysql` (v10.11.0)

#### payment-service (NUEVO)
- **`payment-service/Dockerfile`**: Multi-stage Dockerfile (Maven + Temurin 21)
- **`payment-service/.dockerignore`**: Excluye archivos innecesarios
- **`payment-service/src/main/resources/application-mysql.yml`**: Configuración MySQL creada para la BD `payments_db` con usuario dedicado

### Nivel de Raíz

- **`docker-compose.yml`**: Orquestación de MySQL, doctor-service, agenda-service, appointment-service, patient-service y payment-service
- **`init-db.sql`**: Script SQL para crear BDs y usuarios

## Configuración de Base de Datos

La setup actual crea **cinco bases de datos separadas**:

| Servicio | BD | Usuario | Contraseña | Puerto (host) |
|----------|----|---------| -----------|---------------|
| doctor-service | `doctors_db` | `doctors` | `doctors123` | 3307 |
| agenda-service | `agenda_db` | `agenda` | `agenda123` | 3307 |
| appointment-service | `appointment_db` | `appointment` | `appointment123` | 3307 |
| patient-service | `patients_db` | `patients` | `patients123` | 3307 |
| payment-service | `payments_db` | `payments` | `payments123` | 3307 |

El script `init-db.sql` se ejecuta automáticamente al inicializar MySQL en la sección `/docker-entrypoint-initdb.d/` del volumen.

## Arquitectura de Build

### Dockerfile Multi-stage (ambos microservicios)
```
Fase 1 (Build):
  - Imagen: maven:3.9-eclipse-temurin-21
  - Copia pom.xml, descarga dependencias
  - Compila código y empaqueta JAR
  
Fase 2 (Runtime):
  - Imagen: eclipse-temurin:21-jre-jammy (ligera)
  - Copia JAR compilado (comodín *.jar)
  - Usuario no-root por seguridad
  - Expone puerto 8082 (doctor), 8085 (agenda), 8087 (appointment), 8081 (patient) o 8084 (payment)
```

## Comandos Rápidos

### Levantar todos los servicios (con build)
```powershell
cd 'E:\Estudio DUOC\Semestre 3\sistema-telemedicina'
docker-compose up --build
```

### Levantar en background
```powershell
docker-compose up -d
```

### Detener servicios
```powershell
docker-compose down
```

### Ver logs
```powershell
# Ver todos los logs
docker-compose logs

# Ver logs en tiempo real
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs doctor
docker-compose logs agenda
docker-compose logs appointment
docker-compose logs patient
docker-compose logs payment
docker-compose logs mysql
```

### Reconstruir una imagen específica
```powershell
docker-compose build --no-cache doctor
docker-compose build --no-cache agenda
docker-compose build --no-cache appointment
docker-compose build --no-cache patient
docker-compose build --no-cache payment
```

## Acceso a los Servicios

### Doctor Service
- **API REST**: http://localhost:8082/doctorservice/api/doctors
- **Swagger UI**: http://localhost:8082/doctorservice/swagger-ui.html

### Agenda Service
- **API REST**: http://localhost:8085/agendaservice/api/...
- **Swagger UI**: http://localhost:8085/agendaservice/swagger-ui.html

### Appointment Service
- **API REST**: http://localhost:8087/api/appointments
- **Swagger UI**: http://localhost:8087/doc/swagger-ui/index.html

### Patient Service
- **API REST**: http://localhost:8081/api/patients
- **Swagger UI**: http://localhost:8081/swagger-ui/index.html
- **H2 Console**: http://localhost:8081/h2-console

### Payment Service (NUEVO)
- **API REST**: http://localhost:8084/api/payments
- **Swagger UI**: http://localhost:8084/swagger-ui/index.html
- **H2 Console**: http://localhost:8084/h2-console

### MySQL (desde host)
```
Host: localhost
Puerto: 3307
```

**BD doctors_db**:
```sql
mysql -h localhost -P 3307 -u doctors -pdoctors123 doctors_db
```

**BD agenda_db**:
```sql
mysql -h localhost -P 3307 -u agenda -pagenda123 agenda_db
```

**BD appointment_db**:
```sql
mysql -h localhost -P 3307 -u appointment -pagenda123 appointment_db
```

**BD patients_db**:
```sql
mysql -h localhost -P 3307 -u patients -ppatients123 patients_db
```

**BD payments_db**:
```sql
mysql -h localhost -P 3307 -u payments -ppayments123 payments_db
```

## Estado Actual

✅ **MySQL 8.0**
- Puerto: 3307 (host) → 3306 (contenedor)
- BDs: `doctors_db`, `agenda_db`, `appointment_db`, `patients_db`, `payments_db`
- Usuarios: `doctors`, `agenda`, `appointment`, `patients`, `payments`
- Estado: **Healthy** ✓

✅ **Doctor Service**
- Puerto: 8082
- Java: 21.0.11
- Perfil: `mysql`
- BD: `doctors_db`
- Estado: **Ejecutándose** ✓

✅ **Agenda Service**
- Puerto: 8085
- Java: 21.0.11
- Perfil: `mysql`
- BD: `agenda_db`
- Estado: **Ejecutándose** ✓

✅ **Appointment Service**
- Puerto: 8087
- Java: 21.0.11
- Perfil: `mysql`
- BD: `appointment_db`
- Estado: **Ejecutándose** ✓

✅ **Patient Service**
- Puerto: 8081
- Java: 21.0.11
- Perfil: `mysql`
- BD: `patients_db`
- Estado: **Ejecutándose** ✓

✅ **Payment Service** (NUEVO)
- Puerto: 8084
- Java: 21.0.11
- Perfil: `mysql`
- BD: `payments_db`
- Estado: **Pendiente de construir**

## Solución de Problemas

### Error: "Unsupported Database: MySQL 8.0"
**Causa**: Falta la dependencia `flyway-mysql` en el pom.xml
**Solución**: Asegurar que `pom.xml` incluye:
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
    <version>10.11.0</version>
</dependency>
```

### Error: "Access denied for user"
**Causa**: El script `init-db.sql` no se ejecutó correctamente
**Solución**:
1. Verificar que `init-db.sql` está en la raíz
2. Ejecutar manualmente en el contenedor MySQL:
```powershell
docker-compose exec mysql mysql -uroot -proot123 -e "
CREATE DATABASE IF NOT EXISTS agenda_db;
CREATE DATABASE IF NOT EXISTS appointment_db;
CREATE DATABASE IF NOT EXISTS patients_db;
CREATE DATABASE IF NOT EXISTS payments_db;
CREATE USER IF NOT EXISTS 'agenda'@'%' IDENTIFIED BY 'agenda123';
CREATE USER IF NOT EXISTS 'appointment'@'%' IDENTIFIED BY 'appointment123';
CREATE USER IF NOT EXISTS 'patients'@'%' IDENTIFIED BY 'patients123';
CREATE USER IF NOT EXISTS 'payments'@'%' IDENTIFIED BY 'payments123';
GRANT ALL PRIVILEGES ON agenda_db.* TO 'agenda'@'%';
GRANT ALL PRIVILEGES ON appointment_db.* TO 'appointment'@'%';
GRANT ALL PRIVILEGES ON patients_db.* TO 'patients'@'%';
GRANT ALL PRIVILEGES ON payments_db.* TO 'payments'@'%';
FLUSH PRIVILEGES;
"
```

### Error: "Connection refused"
**Causa**: URL de conexión usa puerto incorrecto
**Solución**: En Docker, usar `mysql:3306` (puerto interno), no `localhost:3307`

## Configuración Avanzada

### Variables de Entorno (docker-compose.yml)
```yaml
SPRING_PROFILES_ACTIVE: mysql          # Perfil de Spring
JAVA_OPTS: "-Xms512m -Xmx1g"          # Opciones JVM
SPRING_DATASOURCE_URL: jdbc:mysql://...  # URL de BD interna
SPRING_DATASOURCE_USERNAME: ...        # Usuario
SPRING_DATASOURCE_PASSWORD: ...        # Contraseña
```

### Cambiar Credenciales de BD
Editar en `docker-compose.yml` y `init-db.sql`, luego ejecutar:
```powershell
docker-compose down
docker volume rm sistema-telemedicina_mysql_data
docker-compose up --build
```

## Notas

- Las migraciones de Flyway están deshabilitadas (no hay archivos V*.sql)
- Hibernate maneja la creación de esquemas automáticamente (`ddl-auto: update`)
- Cada microservicio tiene su propia BD para evitar acoplamiento
- El archivo `docker-compose.yml` es el entry point; el script `init-db.sql` se ejecuta automáticamente



## Archivos Generados

Se han creado los siguientes archivos para la integración con Docker:

### 1. **`doctor-service/Dockerfile`**
Dockerfile multi-stage optimizado:
- **Fase Build**: Usa `maven:3.9-eclipse-temurin-21` para compilar el código
  - Copia `pom.xml` y descarga dependencias (aprovecha caché)
  - Compila el código fuente y genera el JAR
- **Fase Runtime**: Usa `eclipse-temurin:21-jre-jammy` (imagen ligera)
  - Copia el JAR generado
  - Ejecuta con usuario no-root por seguridad

### 2. **`doctor-service/.dockerignore`**
Archivo que excluye del contexto de build:
- `target/` (artefactos compilados)
- `.git/`, `.idea/`, `*.iml` (archivos IDE)
- `logs/` (archivos de log)

### 3. **`docker-compose.yml`** (en la raíz del repositorio)
Orquestación de servicios:

#### Servicio MySQL
- **Imagen**: `mysql:8.0`
- **Puerto**: `3307:3306` (host:contenedor)
- **BD**: `doctors_db`
- **Usuario**: `doctors` / **Contraseña**: `doctors123`
- **Volumen**: `mysql_data:/var/lib/mysql` (persistencia)
- **Healthcheck**: Verifica que MySQL está listo

#### Servicio Doctor
- **Imagen**: `doctor-service:latest` (construida localmente)
- **Puerto**: `8082:8082` (host:contenedor)
- **Perfil**: `mysql`
- **Variables de entorno**:
  - `SPRING_DATASOURCE_URL`: `jdbc:mysql://mysql:3306/doctors_db...`
  - `SPRING_DATASOURCE_USERNAME`: `doctors`
  - `SPRING_DATASOURCE_PASSWORD`: `doctors123`
  - `JAVA_OPTS`: `-Xms512m -Xmx1g`
- **Depende de**: MySQL (espera a que esté healthy)

## Requisitos Previos

- Docker Desktop instalado y corriendo
- Docker Compose (incluido en Docker Desktop)
- 2GB de RAM disponible mínimo

## Comandos Útiles

### Levantar los servicios (primero construye la imagen)
```powershell
cd 'E:\Estudio DUOC\Semestre 3\sistema-telemedicina'
docker-compose up --build
```

### Levantar los servicios en background
```powershell
docker-compose up -d
```

### Detener los servicios
```powershell
docker-compose down
```

### Ver logs del servicio doctor
```powershell
docker-compose logs doctor
```

### Ver logs en tiempo real
```powershell
docker-compose logs -f doctor
```

### Verificar estado de los servicios
```powershell
docker-compose ps
```

## Acceso a los Servicios

### API REST (doctor-service)
```
http://localhost:8082/doctorservice/api/doctors
```

### Swagger UI
```
http://localhost:8082/doctorservice/swagger-ui.html
```

### H2 Console (solo para perfil h2)
```
http://localhost:8082/doctorservice/h2-console
```

### MySQL (desde host)
```
Host: localhost
Puerto: 3307
Usuario: doctors
Contraseña: doctors123
BD: doctors_db
```

## Solución de Problemas

### Error: "error: release version 21 not supported"
**Causa**: El Dockerfile estaba usando Java 17 en lugar de Java 21
**Solución**: Se ha actualizado a `maven:3.9-eclipse-temurin-21` y `eclipse-temurin:21-jre-jammy`

### Error: "Communications link failure" en la BD
**Causa**: La URL de conexión usaba puerto incorrecto (3307 en lugar de 3306 dentro del contenedor)
**Solución**: Se cambió en docker-compose.yml a `jdbc:mysql://mysql:3306/doctors_db...`

### Error: "Cannot connect to port 8080"
**Causa**: El servicio estaba configurado en puerto 8082, no 8080
**Solución**: Se actualizó docker-compose.yml para mapear `8082:8082`

## Configuración Adicional

### Para cambiar credenciales de MySQL
Edita `docker-compose.yml` en la sección del servicio `mysql`:
```yaml
environment:
  MYSQL_DATABASE: doctors_db
  MYSQL_USER: doctors
  MYSQL_PASSWORD: doctors123
  MYSQL_ROOT_PASSWORD: root123
```

También actualiza las variables en el servicio `doctor`:
```yaml
SPRING_DATASOURCE_USERNAME: doctors
SPRING_DATASOURCE_PASSWORD: doctors123
```

### Para cambiar memoria JVM
Edita en `docker-compose.yml`:
```yaml
JAVA_OPTS: "-Xms512m -Xmx1g"
```

## Notas de Desarrollo

- Las migraciones de Flyway están deshabilitadas (no hay archivos V*.sql)
- Se crea automáticamente la estructura BD via Hibernate (`ddl-auto: update`)
- Los datos iniciales se cargan via `DataInitializer.java`
- El servicio usa perfil `mysql` (ver `application-mysql.yml`)

## Características de Producción Consideradas

✅ Multi-stage build para imagen mínima
✅ Usuario no-root en la imagen
✅ Healthcheck para MySQL
✅ Restart policies configuradas
✅ Variables de entorno configurables
✅ Volumen persistente para BD
✅ Red bridge explícita entre servicios


