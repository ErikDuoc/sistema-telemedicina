# Docker Setup - doctor-service

Este documento explica cómo ejecutar el microservicio `doctor-service` con Docker Compose.

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


