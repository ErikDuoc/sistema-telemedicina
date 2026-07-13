# Documentación Técnica - Sistema de Telemedicina

## 1. Arquitectura General

### 1.1 Componentes

- **API Gateway** (8080): Punto de entrada centralizado (Spring Cloud Gateway)
- **Eureka Server** (8761): Service Discovery (Netflix Eureka)
- **10 Microservicios** (8081-8091): Servicios de dominio
- **MySQL** (3307 local): Base de datos con 10 esquemas separados

### 1.2 Flujo de Comunicación

```
Cliente HTTP
    │
    ▼
API Gateway (8080) - lb://service-name
    │
    ├─ Eureka (8761)
    │   └─ ¿Dónde está patient-service?
    │   └─ Respuesta: 8081
    │
    ▼
Microservicio (8081)
    │
    ├─ Feign Client
    │   └─ Llama a otro servicio via Eureka
    │
    ▼
Respuesta JSON
```

## 2. Microservicios

| Servicio | Puerto | BD | Responsabilidad |
|---|---|---|---|
| patient-service | 8081 | patients_db | Gestión de pacientes |
| doctor-service | 8082 | doctors_db | Catálogo de médicos |
| agenda-service | 8085 | agenda_db | Disponibilidad de médicos |
| appointment-service | 8087 | appointment_db | Creación y gestión de citas |
| clinical-record-service | 8088 | clinical_records_db | Registros clínicos |
| laboratory-service | 8086 | lab_db | Órdenes y resultados de lab |
| prescription-service | 8089 | prescriptions_db | Recetas médicas |
| video-consultation-service | 8091 | video_consultations_db | Consultas por video |
| payment-service | 8084 | payments_db | Procesamiento de pagos |
| notification-service | 8083 | notifications_db | Notificaciones |

## 3. Modelo de Datos

### 3.1 Entidades y Relaciones

```
patients (1) ─────────────── (N) appointments
                                  ├─ (1) doctors
                                  └─ (N) video_consultations

patients (1) ─────────────── (N) clinical_records
patients (1) ─────────────── (N) lab_orders
patients (1) ─────────────── (N) prescriptions

doctors (1) ──────────────── (N) appointments
doctors (1) ──────────────── (N) doctor_agendas
doctors (1) ──────────────── (N) prescriptions

insurances (1) ─────────────── (N) payments
```

### 3.2 Tablas Principales

- **patients**: Información de pacientes
- **doctors**: Catálogo de médicos con especialización
- **doctor_agendas**: Disponibilidad semanal de médicos
- **appointments**: Citas médicas programadas
- **clinical_records**: Diagnósticos y tratamientos
- **lab_orders**: Órdenes de laboratorio
- **lab_results**: Resultados de laboratorio
- **prescriptions**: Recetas médicas
- **video_consultations**: Sesiones de video
- **payments**: Procesamiento de pagos
- **insurances**: Planes de seguros
- **notifications**: Notificaciones del sistema

## 4. Migraciones Flyway

### 4.1 Estrategia de Migraciones

Cada servicio incluye migraciones versionadas en `src/main/resources/db/migration/`:

- **V1__***: Crear tablas base
- **V2__***: Insertar datos iniciales (seeds)
- **V3__* y superiores**: Cambios posteriores (si aplica)

### 4.2 Control de Migraciones

Flyway mantiene un registro en la tabla `flyway_schema_history` con:
- `version`: Número de versión (V1, V2, etc.)
- `description`: Descripción de la migración
- `installed_on`: Fecha de instalación
- `execution_time`: Tiempo de ejecución
- `success`: Estado (true/false)

### 4.3 Configuración Flyway en application.yml

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
  jpa:
    hibernate:
      ddl-auto: validate  # No crear/actualizar esquema, solo validar
```

## 5. Ejecución desde Cero

### 5.1 Requisitos

- Java 21+
- Maven 3.8+
- Docker + Docker Compose
- MySQL 8.0+ (o Docker)

### 5.2 Pasos Iniciales

```bash
# 1. Clonar repositorio
git clone <repo>
cd sistema-telemedicina

# 2. Crear archivo .env desde .env.example
cp .env.example .env

# 3. Compilar todo el proyecto
mvn clean install

# 4. Levantar servicios con Docker Compose
docker-compose up -d

# 5. Esperar a que los servicios se registren en Eureka
sleep 60

# 6. Verificar que Eureka esté disponible
curl http://localhost:8761/eureka/apps

# 7. Verificar que el gateway enruta correctamente
curl http://localhost:8080/api/patients
```

### 5.3 Pasos en Desarrollo (sin Docker)

```bash
# Inicializar MySQL
mysql -u root -p < init-db.sql

# Ejecutar cada servicio en orden (en terminales separadas)
cd patient-service && SPRING_PROFILES_ACTIVE=mysql mvn spring-boot:run
cd doctor-service && SPRING_PROFILES_ACTIVE=mysql mvn spring-boot:run
# ... etc para los demás servicios

# O directamente:
mvn clean install
java -jar patient-service/target/patient-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=mysql
```

## 6. Perfiles de Spring

### 6.1 Perfil `h2` (Desarrollo/Testing)

- BD en memoria H2
- Flyway deshabilitado automáticamente para tests
- Ideal para tests rápidos sin dependencias externas

### 6.2 Perfil `mysql` (Producción/Staging)

- Conexión a MySQL real
- Flyway ejecuta migraciones automáticamente
- Variables de entorno para credenciales

### 6.3 Cambiar Perfil

```bash
# Vía variable de entorno
export SPRING_PROFILES_ACTIVE=mysql
java -jar app.jar

# Vía argumento Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mysql"

# Vía docker-compose.yml
environment:
  SPRING_PROFILES_ACTIVE: mysql
```

## 7. Autenticación y Seguridad

### 7.1 JWT Token

Cada solicitud requiere token JWT en header:

```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/patients
```

### 7.2 Roles

- **ADMIN**: Acceso completo
- **DOCTOR**: Gestión de citas y registros clínicos
- **PATIENT**: Solo acceso a datos propios

### 7.3 Variables de Entorno de Seguridad

```env
JWT_SECRET=your-super-secret-key-min-32-chars
JWT_EXPIRATION_HOURS=24
```

## 8. Comunicación entre Servicios (Feign)

Los microservicios se comunican vía Feign Clients:

```java
@FeignClient(name = "patient-service")
public interface PatientClient {
    @GetMapping("/api/patients/{id}")
    PatientDTO getPatient(@PathVariable Long id);
}
```

El Gateway automáticamente resuelve los nombres de servicio vía Eureka.

## 9. Swagger/OpenAPI

Cada servicio publica su API en Swagger:

- **Patient Service**: http://localhost:8081/swagger-ui.html
- **Doctor Service**: http://localhost:8082/swagger-ui.html
- ... etc

## 10. Troubleshooting

### Problema: Servicio no se registra en Eureka

```bash
# Ver logs
docker-compose logs patient | grep -i eureka

# Verificar variable de entorno
echo $EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
```

**Solución**: Asegurar que la URL de Eureka es correcta en .env y docker-compose.yml

### Problema: Gateway retorna 404

```bash
# Listar rutas registradas
curl http://localhost:8080/actuator/gateway/routes

# Verificar que rutas usen formato lb://
# Ejemplo: http://lb://patient-service/api/patients
```

**Solución**: Confirmar que Gateway tiene rutas configuradas correctamente

### Problema: Flyway falla al migrar

```bash
# Ver logs de Flyway
docker-compose logs patient | grep -i flyway

# Verificar sintaxis SQL
cat patient-service/src/main/resources/db/migration/V1__*.sql
```

**Solución**: 
1. Revisar sintaxis SQL en migraciones
2. Verificar que tabla no exista (Flyway crea tabla flyway_schema_history)
3. Limpiar base de datos: `DROP DATABASE patient_db; CREATE DATABASE patient_db;`

### Problema: "Table doesn't exist"

**Causa**: Flyway no ejecutó las migraciones

```bash
# Verificar tabla flyway_schema_history
docker-compose exec mysql mysql -upatients -ppatients123 -e "SELECT * FROM patients_db.flyway_schema_history;"

# Si está vacía, ejecutar manualmente
docker-compose exec patient bash -c "java -Dspring.flyway.locations=classpath:db/migration -jar app.jar"
```

## 11. Logs y Monitoreo

### 11.1 Ver Logs

```bash
# Docker Compose
docker-compose logs -f patient

# Archivo local
tail -f logs/patient-service.log

# Con filtro
docker-compose logs patient | grep -i error
```

### 11.2 Actuator Endpoints

```bash
# Health
curl http://localhost:8081/actuator/health

# Información de la app
curl http://localhost:8081/actuator/info

# Métricas
curl http://localhost:8081/actuator/metrics
```

## 12. Base de Datos

### 12.1 Conexión Directa

```bash
# Desde Docker
docker-compose exec mysql mysql -uroot -proot123

# Desde host (puerto mapeado a 3307)
mysql -h localhost -P 3307 -u patients -ppatients123 patients_db

# Ver tablas
USE patients_db;
SHOW TABLES;
DESCRIBE patients;
```

### 12.2 Respaldo de BD

```bash
# Crear backup
docker-compose exec mysql mysqldump -uroot -proot123 --all-databases > backup.sql

# Restaurar
docker-compose exec -T mysql mysql -uroot -proot123 < backup.sql
```

## 13. Referencias

- **Spring Cloud**: https://spring.io/projects/spring-cloud
- **Flyway**: https://flywaydb.org/documentation
- **MySQL**: https://dev.mysql.com/doc/
- **Docker Compose**: https://docs.docker.com/compose/

