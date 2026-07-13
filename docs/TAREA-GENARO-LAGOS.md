# 📚 TAREA GENARO LAGOS — Migraciones: Flyway, .env, Documentación

**Rol**: Migraciones, Docker/Render y Documentación  
**Responsabilidad**: Migraciones Flyway, variables de entorno, documentación completa, despliegue Render

---

## OBJETIVO

- ✅ Crear migraciones Flyway versionadas para 10 servicios
- ✅ Crear `.env.example` con 50+ variables
- ✅ Documentación técnica y funcional completa
- ✅ Matriz de requerimientos con trazabilidad
- ✅ Documentar despliegue en Render

---

## 📋 PASO 1: CREAR MIGRACIONES FLYWAY

### 1.1 Para CADA servicio, crear directorio

```bash
mkdir -p patient-service/src/main/resources/db/migration
mkdir -p doctor-service/src/main/resources/db/migration
mkdir -p agenda-service/src/main/resources/db/migration
mkdir -p appointment-service/src/main/resources/db/migration
mkdir -p clinical-record-service/src/main/resources/db/migration
mkdir -p laboratory-service/src/main/resources/db/migration
mkdir -p prescription-service/src/main/resources/db/migration
mkdir -p video-consultation-service/src/main/resources/db/migration
mkdir -p payment-service/src/main/resources/db/migration
mkdir -p notification-service/src/main/resources/db/migration
```

### 1.2 Agregar Flyway a pom.xml

Para CADA servicio, editar `*/pom.xml` y agregar en `<dependencies>`:

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

### 1.3 Crear migraciones por servicio

#### PATIENT SERVICE

**Archivo**: `patient-service/src/main/resources/db/migration/V1__create_patients_table.sql`

```sql
CREATE TABLE IF NOT EXISTS patients (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    date_of_birth DATE,
    rut VARCHAR(12) UNIQUE,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    password VARCHAR(255),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Archivo**: `patient-service/src/main/resources/db/migration/V2__insert_seed_patients.sql`

```sql
INSERT INTO patients (name, email, phone, rut, status, password) VALUES
('Juan Pérez', 'juan@example.com', '+56912345678', '12345678-9', 'ACTIVE', '$2a$10$slYQmyNdGzin7olVN3DOjuK.xCCAi.N1W3uxKxRZLa3c.JfDh/4f6'),
('María García', 'maria@example.com', '+56987654321', '87654321-0', 'ACTIVE', '$2a$10$slYQmyNdGzin7olVN3DOjuK.xCCAi.N1W3uxKxRZLa3c.JfDh/4f6');
```

#### DOCTOR SERVICE

**Archivo**: `doctor-service/src/main/resources/db/migration/V1__create_doctors_table.sql`

```sql
CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    specialization VARCHAR(100) NOT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    phone VARCHAR(20),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    password VARCHAR(255),
    INDEX idx_specialization (specialization),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Archivo**: `doctor-service/src/main/resources/db/migration/V2__insert_seed_doctors.sql`

```sql
INSERT INTO doctors (name, email, specialization, license_number, status, password) VALUES
('Dr. Carlos López', 'carlos@example.com', 'Cardiology', 'LIC001', 'ACTIVE', '$2a$10$slYQmyNdGzin7olVN3DOjuK.xCCAi.N1W3uxKxRZLa3c.JfDh/4f6'),
('Dra. Ana Rodríguez', 'ana@example.com', 'Pediatrics', 'LIC002', 'ACTIVE', '$2a$10$slYQmyNdGzin7olVN3DOjuK.xCCAi.N1W3uxKxRZLa3c.JfDh/4f6');
```

#### APPOINTMENT SERVICE

**Archivo**: `appointment-service/src/main/resources/db/migration/V1__create_appointments_table.sql`

```sql
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date DATETIME NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    type VARCHAR(50) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    INDEX idx_patient (patient_id),
    INDEX idx_doctor (doctor_id),
    INDEX idx_status (status),
    INDEX idx_date (appointment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### AGENDA SERVICE

**Archivo**: `agenda-service/src/main/resources/db/migration/V1__create_agenda_table.sql`

```sql
CREATE TABLE IF NOT EXISTS doctor_agendas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL,
    available_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    max_patients INT DEFAULT 5,
    status VARCHAR(50) DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    INDEX idx_doctor (doctor_id),
    INDEX idx_date (available_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### CLINICAL RECORD SERVICE

**Archivo**: `clinical-record-service/src/main/resources/db/migration/V1__create_clinical_records_table.sql`

```sql
CREATE TABLE IF NOT EXISTS clinical_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    appointment_id BIGINT,
    diagnosis TEXT NOT NULL,
    treatment TEXT,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    INDEX idx_patient (patient_id),
    INDEX idx_appointment (appointment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### LABORATORY SERVICE

**Archivo**: `laboratory-service/src/main/resources/db/migration/V1__create_lab_tables.sql`

```sql
CREATE TABLE IF NOT EXISTS lab_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    order_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    INDEX idx_patient (patient_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS lab_results (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    test_name VARCHAR(255) NOT NULL,
    result TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'COMPLETED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES lab_orders(id) ON DELETE CASCADE,
    INDEX idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### PRESCRIPTION SERVICE

**Archivo**: `prescription-service/src/main/resources/db/migration/V1__create_prescriptions_table.sql`

```sql
CREATE TABLE IF NOT EXISTS prescriptions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    medication VARCHAR(255) NOT NULL,
    dosage VARCHAR(50) NOT NULL,
    duration_days INT NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    INDEX idx_patient (patient_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### VIDEO CONSULTATION SERVICE

**Archivo**: `video-consultation-service/src/main/resources/db/migration/V1__create_video_consultations_table.sql`

```sql
CREATE TABLE IF NOT EXISTS video_consultations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    appointment_id BIGINT NOT NULL,
    room_url VARCHAR(500) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    duration_minutes INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    INDEX idx_appointment (appointment_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### PAYMENT SERVICE

**Archivo**: `payment-service/src/main/resources/db/migration/V1__create_payment_tables.sql`

```sql
CREATE TABLE IF NOT EXISTS insurances (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    coverage_percentage INT DEFAULT 80,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    appointment_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    insurance_id BIGINT,
    status VARCHAR(50) DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (insurance_id) REFERENCES insurances(id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**Archivo**: `payment-service/src/main/resources/db/migration/V2__insert_seed_insurances.sql`

```sql
INSERT INTO insurances (name, coverage_percentage) VALUES
('Fonasa', 70),
('Isapre A', 80),
('Isapre B', 85);
```

#### NOTIFICATION SERVICE

**Archivo**: `notification-service/src/main/resources/db/migration/V1__create_notifications_table.sql`

```sql
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP NULL,
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 1.4 Configurar Flyway en application.yml

Para CADA servicio, editar `*/src/main/resources/application.yml` y actualizar sección `spring`:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
    locations: classpath:db/migration
```

### 1.5 Compilar y verificar

```bash
mvn clean install
```

**Resultado esperado**: `BUILD SUCCESS`

---

## 📋 PASO 2: CREAR .ENV.EXAMPLE

### 2.1 Crear `.env.example` en la raíz

**Archivo**: `.env.example`

```env
# ====== DESCUBRIMIENTO DE SERVICIOS (EUREKA) ======
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/
EUREKA_INSTANCE_PREFER_IP_ADDRESS=true

# ====== MYSQL (CONTENEDOR DOCKER) ======
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver

# ====== JWT ======
JWT_SECRET=your-super-secret-jwt-key-change-in-production-12345
JWT_EXPIRATION_HOURS=24

# ====== SPRING PROFILES ======
SPRING_PROFILES_ACTIVE=mysql
SPRING_DOCKER=false

# ====== PACIENTES ======
PATIENT_DATASOURCE_URL=jdbc:mysql://mysql:3306/patients_db?useSSL=false&allowPublicKeyRetrieval=true
PATIENT_DATASOURCE_USERNAME=patients
PATIENT_DATASOURCE_PASSWORD=patients123

# ====== DOCTORES ======
DOCTOR_DATASOURCE_URL=jdbc:mysql://mysql:3306/doctors_db?useSSL=false&allowPublicKeyRetrieval=true
DOCTOR_DATASOURCE_USERNAME=doctors
DOCTOR_DATASOURCE_PASSWORD=doctors123

# ====== CITAS ======
APPOINTMENT_DATASOURCE_URL=jdbc:mysql://mysql:3306/appointment_db?useSSL=false&allowPublicKeyRetrieval=true
APPOINTMENT_DATASOURCE_USERNAME=appointment
APPOINTMENT_DATASOURCE_PASSWORD=appointment123

# ====== REGISTROS CLÍNICOS ======
CLINICAL_RECORD_DATASOURCE_URL=jdbc:mysql://mysql:3306/clinical_records_db?useSSL=false&allowPublicKeyRetrieval=true
CLINICAL_RECORD_DATASOURCE_USERNAME=clinical_records
CLINICAL_RECORD_DATASOURCE_PASSWORD=clinical123

# ====== LABORATORIO ======
LAB_DATASOURCE_URL=jdbc:mysql://mysql:3306/lab_db?useSSL=false&allowPublicKeyRetrieval=true
LAB_DATASOURCE_USERNAME=lab
LAB_DATASOURCE_PASSWORD=lab123

# ====== PRESCRIPCIONES ======
PRESCRIPTION_DATASOURCE_URL=jdbc:mysql://mysql:3306/prescriptions_db?useSSL=false&allowPublicKeyRetrieval=true
PRESCRIPTION_DATASOURCE_USERNAME=prescriptions
PRESCRIPTION_DATASOURCE_PASSWORD=prescriptions123

# ====== VIDEO CONSULTAS ======
VIDEO_DATASOURCE_URL=jdbc:mysql://mysql:3306/video_consultations_db?useSSL=false&allowPublicKeyRetrieval=true
VIDEO_DATASOURCE_USERNAME=video_consultations
VIDEO_DATASOURCE_PASSWORD=video_consultations123

# ====== PAGOS ======
PAYMENT_DATASOURCE_URL=jdbc:mysql://mysql:3306/payments_db?useSSL=false&allowPublicKeyRetrieval=true
PAYMENT_DATASOURCE_USERNAME=payments
PAYMENT_DATASOURCE_PASSWORD=payments123

# ====== AGENDA ======
AGENDA_DATASOURCE_URL=jdbc:mysql://mysql:3306/agenda_db?useSSL=false&allowPublicKeyRetrieval=true
AGENDA_DATASOURCE_USERNAME=agenda
AGENDA_DATASOURCE_PASSWORD=agenda123

# ====== NOTIFICACIONES ======
NOTIFICATION_DATASOURCE_URL=jdbc:mysql://mysql:3306/notifications_db?useSSL=false&allowPublicKeyRetrieval=true
NOTIFICATION_DATASOURCE_USERNAME=notifications
NOTIFICATION_DATASOURCE_PASSWORD=notifications123
```

### 2.2 Actualizar docker-compose.yml

El archivo `docker-compose.yml` debe actualizado por Erik. Verificar que incluye variables `environment` referenciadas del `.env`.

---

## 📋 PASO 3: DOCUMENTACIÓN TÉCNICA

### 3.1 Crear `docs/documentacion-tecnica.md`

```markdown
# Documentación Técnica - Sistema de Telemedicina

## 1. Arquitectura General

### 1.1 Componentes

- **API Gateway** (8080): Punto de entrada centralizado (Spring Cloud Gateway)
- **Eureka Server** (8761): Service Discovery (Netflix Eureka)
- **10 Microservicios** (8081-8091): Servicios de dominio
- **MySQL** (3307 local): Base de datos única con 10 esquemas

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

## 4. Migraciones Flyway

Cada servicio incluye migraciones versionadas:
- **V1__***: Crear tablas base
- **V2__***: Insertar datos iniciales (seeds)

Ejecutadas automáticamente al arrancar cada servicio.

## 5. Ejecución desde Cero

### 5.1 Requisitos

- Java 21
- Maven 3.8+
- Docker + Docker Compose

### 5.2 Pasos

```bash
# 1. Clonar
git clone <repo>
cd sistema-telemedicina

# 2. Variables de entorno
cp .env.example .env

# 3. Compilar
mvn clean install

# 4. Levantar servicios
docker-compose up -d

# 5. Esperar arranque
sleep 60

# 6. Verificar
curl http://localhost:8761/eureka/apps
curl http://localhost:8080/api/patients
```

## 6. Troubleshooting

### Servicio no se registra en Eureka
```bash
docker-compose logs patient | grep -i eureka
# Verificar que EUREKA_CLIENT_SERVICEURL_DEFAULTZONE esté correcto
```

### Gateway retorna 404
```bash
curl http://localhost:8080/actuator/gateway/routes
# Verificar que rutas están en format lb://
```

### Flyway falla
```bash
docker-compose logs patient | grep -i flyway
# Verificar sintaxis SQL en V1__*.sql
```
```

---

## 📋 PASO 4: DOCUMENTACIÓN FUNCIONAL

### 4.1 Crear `docs/documentacion-funcional.md`

```markdown
# Documentación Funcional - Sistema de Telemedicina

## 1. Problema Resuelto

Plataforma integral de telemedicina que permite:
- Pacientes agendar citas con médicos especializados
- Médicos gestionar disponibilidad y atender pacientes
- Registro de diagnósticos, recetas y resultados de laboratorio
- Notificaciones automáticas
- Procesamiento seguro de pagos

## 2. Actores

### Paciente
- Ver médicos disponibles por especialidad
- Agendar cita
- Ver citas próximas
- Acceder a registros clínicos propios
- Ver recetas y resultados de laboratorio

### Médico
- Definir disponibilidad semanal
- Ver citas asignadas
- Crear diagnósticos
- Prescribir medicamentos
- Ordenar laboratorios

### Administrador
- Gestionar usuarios (crear, desactivar)
- Configurar seguros y planes de pago
- Ver reportes de auditoría

## 3. Flujos Principales

### Flujo: Agendar Cita

1. Paciente accede a Gateway (http://8080)
2. Login → obtiene JWT token
3. Listar médicos (GET /api/doctors?specialization=Cardiology)
4. Ver disponibilidad (GET /api/agenda/doctor/{id})
5. Crear cita (POST /api/appointments)
   - Sistema valida disponibilidad via Feign
   - Crea registro en BD
   - Envía notificación
6. Retorna 201 + datos cita

### Flujo: Consulta Médica

1. Médico accede a cita
2. Inicia video consulta (POST /api/video-consultations)
3. Obtiene URL de sala
4. Consulta se realiza
5. Médico registra diagnóstico
6. Paciente recibe notificación

## 4. Requerimientos Funcionales

| ID | Requerimiento | Endpoint | Status |
|----|---|---|---|
| RF-01 | Crear paciente | POST /api/patients | ✅ |
| RF-02 | Listar pacientes | GET /api/patients | ✅ |
| RF-03 | Obtener paciente | GET /api/patients/{id} | ✅ |
| RF-04 | Actualizar paciente | PUT /api/patients/{id} | ✅ |
| RF-05 | Crear cita | POST /api/appointments | ✅ |
| ... | ... | ... | ✅ |
```

---

## 📋 PASO 5: MATRIZ DE REQUERIMIENTOS

### 5.1 Crear `docs/matriz-requerimientos.md`

```markdown
# Matriz de Requerimientos - Sistema de Telemedicina

| ID | Requerimiento | Tipo | Estado | Endpoint | Prueba | Evidencia |
|----|---|---|---|---|---|---|
| RF-01 | Crear paciente | Funcional | ✅ | POST /api/patients | PatientControllerTest | patient-service/src/test |
| RF-02 | Listar pacientes | Funcional | ✅ | GET /api/patients | PatientControllerTest | patient-service/src/test |
| RN-01 | Email único | Negocio | ✅ | Validación en Service | crearPaciente_emailDuplicado | patient-service/src/main/java |
| INF-01 | API Gateway | Infra | ✅ | http://8080 | curl exitoso | gateway-service/application.yml |
| INF-02 | Eureka Server | Infra | ✅ | http://8761 | Dashboard UP | discovery-server/application.yml |
| INF-03 | Migraciones Flyway | Infra | ✅ | */db/migration/*.sql | docker-compose logs | */src/main/resources/db/migration |
| INF-04 | Feign Discovery | Infra | ✅ | @FeignClient(name=) | PatientClientIT | */src/main/java/*/client |
| INF-05 | Docker Compose | Infra | ✅ | docker-compose up | docker-compose ps | docker-compose.yml |
| INF-06 | Swagger/OpenAPI | Infra | ✅ | /swagger-ui.html | Acceso UI | */config/OpenApiConfig |
| INF-07 | Tests (80%) | Infra | ✅ | mvn test | BUILD SUCCESS | */src/test/java |
| INF-08 | Despliegue Render | Infra | ✅ | https://patient-service-*.render.com | Health 200 | Render console |
```

---

## 📋 PASO 6: ACTUALIZAR README.md

Editar `README.md` raíz y agregar:

```markdown
## 🚀 Inicio Rápido (Docker Compose)

\`\`\`bash
cp .env.example .env
docker-compose up -d
sleep 60
curl http://localhost:8761/eureka/apps
\`\`\`

## 🌐 Accesos

- API Gateway: http://localhost:8080
- Eureka Dashboard: http://localhost:8761
- Swagger (Patient): http://localhost:8081/swagger-ui.html
```

---

## 📋 PASO 7: DOCUMENTO DE DEFENSA INDIVIDUAL

### 7.1 Crear `docs/defensa-individual/Genaro-Lagos.md`

```markdown
# Defensa Individual - Genaro Lagos

## Tareas Realizadas

### Migraciones Flyway
- **Servicios**: 10
- **Archivos creados**: V1__*.sql (10), V2__*.sql (4)
- **Total migraciones**: ~20 archivos SQL
- **Commits**: [HASH-1] crear migraciones, [HASH-2] agregar Flyway deps

### Variables de Entorno
- **Archivo .env.example**: Creado con 50+ variables
- **Commits**: [HASH-3] crear .env.example

### Documentación
- **Archivos creados**:
  - documentacion-tecnica.md
  - documentacion-funcional.md
  - matriz-requerimientos.md
  - despliegue-render.md (si aplica)
- **README.md actualizado**
- **Commits**: [HASH-4] docs técnica, [HASH-5] docs funcional

## Evidencias

### Migraciones Ejecutándose
```bash
docker-compose logs patient | grep "flyway\|migration"
# Resultado: Successfully applied 2 migrations
```

### Datos Cargados
```bash
curl http://localhost:8081/api/patients
# Resultado: [{"id":1,"name":"Juan Pérez",...}]
```

### Documentación Completa
```bash
ls -la docs/ | grep matriz
# Resultado: matriz-requerimientos.md existe
```

## Conceptos Técnicos

### ¿Diferencia entre ddl-auto vs Flyway?

**ddl-auto=create**: 
- Hibernate crea esquema desde cero
- PELIGRO: Puede borrar datos
- No versionado
- No reproducible en múltiples instancias

**Flyway**:
- Control explícito de migraciones
- Versionado (V1__, V2__, etc.)
- Tabla flyway_schema_history
- Reproducible y seguro

### ¿Qué tabla usa Flyway?

**flyway_schema_history** - Registro de todas las migraciones aplicadas
```sql
SELECT * FROM flyway_schema_history;
-- Muestra: version, description, installed_on, etc.
```
```

---

## ✅ CHECKLIST FINAL

- [ ] Flyway agregado a 10 pom.xml
- [ ] Migraciones V1__*.sql creadas (10)
- [ ] Migraciones V2__*.sql creadas (4)
- [ ] Configuración Flyway en 10 application.yml
- [ ] .env.example creado con 50+ variables
- [ ] docs/documentacion-tecnica.md completo
- [ ] docs/documentacion-funcional.md completo
- [ ] docs/matriz-requerimientos.md completo
- [ ] README.md actualizado con instrucciones
- [ ] docs/defensa-individual/Genaro-Lagos.md creado
- [ ] `mvn clean install` compila sin errores
- [ ] `docker-compose up -d && sleep 60` levanta sin errores
- [ ] `curl http://localhost:8081/api/patients` retorna datos
- [ ] Logs muestran Flyway ejecutando migraciones
- [ ] Commits pusheados con mensajes significativos

---

## 🔄 COORDINACIÓN FINAL

**Después de completar tu tarea:**

1. **Comunica a Erik y Miguel**: "Migraciones y documentación listos"
2. **Espera confirmación**: Ambos terminan
3. **Ejecución conjunta**:
   ```bash
   docker-compose down
   docker-compose up -d
   sleep 60
   # Verificar todo
   curl http://localhost:8761/eureka/apps
   curl http://localhost:8080/api/patients
   ```
4. **Actualizar matriz**: Erik agrega evidencias finales (URLs Render, logs, etc.)

---

**¡Éxito! 📚**

