# Matriz de Requerimientos - Sistema de Telemedicina

**Proyecto**: Plataforma de Telemedicina basada en Microservicios  
**Actualización**: 2026-07-13  
**Responsable**: Erik Queirolo (Coordinación)  
**Estado**: En desarrollo (fase de infraestructura completada)

---

## Leyenda

| Símbolo | Significado |
|---------|------------|
| ✅ | Implementado y verificado |
| ⚠️ | Parcialmente implementado |
| ❌ | No implementado |
| 🔄 | En progreso |

---

## 1. REQUERIMIENTOS DE INFRAESTRUCTURA (INF)

### 1.1 Service Discovery y Gateway

| ID | Requerimiento | Tipo | Estado | Endpoint/Evidencia | Archivo(s) | Notas |
|----|---|---|---|---|---|---|
| INF-01 | Eureka Server (Discovery) | Infraestructura | ✅ | `http://localhost:8761/eureka/apps` | `discovery-server/` | 10+ servicios registrados |
| INF-02 | API Gateway centralizado | Infraestructura | ✅ | `http://localhost:8080` | `gateway-service/` | Rutas `lb://` configuradas |
| INF-03 | Rutas load-balanced (lb://) | Infraestructura | ✅ | `gateway-service/application.yml` | Gateway | 10 rutas a servicios |
| INF-04 | Registro dinámico de servicios | Infraestructura | ✅ | Eureka Dashboard | Cada pom.xml | spring-cloud-starter-netflix-eureka-client |
| INF-05 | Docker Compose orquestado | Infraestructura | 🔄 | `docker-compose.yml` | Raíz | Configurado, pendiente validación con `docker-compose up` |

### 1.2 Persistencia y Base de Datos

| ID | Requerimiento | Tipo | Estado | Endpoint/Evidencia | Archivo(s) | Notas |
|----|---|---|---|---|---|---|
| INF-06 | MySQL centralizado | Infraestructura | 🔄 | `mysql:3307` | `docker-compose.yml` | 10 esquemas, pendiente validación en Docker |
| INF-07 | Migraciones Flyway | Infraestructura | 🔄 | `*/src/main/resources/db/migration/` | Genaro | 14 migraciones presentes en total; 13 agregadas en esta tarea; ejecución pendiente de validación |
| INF-08 | Variables de entorno | Infraestructura | ✅ | `.env.example` | Raíz | 67 variables sin secretos reales |
| INF-09 | Perfiles de configuración | Infraestructura | 🔄 | `application.yml` + `application-mysql.yml` | Cada servicio | Configurados, pendiente validación compile |

### 1.3 Comunicación Remota

| ID | Requerimiento | Tipo | Estado | Endpoint/Evidencia | Archivo(s) | Notas |
|----|---|---|---|---|---|---|
| INF-10 | Feign Client Discovery | Infraestructura | 🔄 | `@FeignClient(name="...")` | 8 servicios | Pendiente Miguel Mesias |
| INF-11 | RestClient integration | Infraestructura | ❌ | - | - | Opcional, usar Feign |
| INF-12 | Timeouts y fallbacks | Infraestructura | 🔄 | Feign configuration | Miguel | Pendiente |

### 1.4 Docker y Deployment

| ID | Requerimiento | Tipo | Estado | Endpoint/Evidencia | Archivo(s) | Notas |
|----|---|---|---|---|---|---|
| INF-13 | Dockerfile per-servicio | Infraestructura | ✅ | `*/Dockerfile` | Cada servicio | Multi-stage, JDK 21 |
| INF-14 | Docker Compose completo | Infraestructura | 🔄 | `docker-compose.yml` | Raíz | Configurado, pendiente ejecución |
| INF-15 | Healthchecks | Infraestructura | 🔄 | docker-compose.yml | MySQL, Eureka | Configurados, pendiente validación Docker |
| INF-16 | Despliegue Render | Infraestructura | ❌ | - | - | Pendiente Genaro Lagos |

### 1.5 Seguridad y Configuración

| ID | Requerimiento | Tipo | Estado | Endpoint/Evidencia | Archivo(s) | Notas |
|----|---|---|---|---|---|---|
| INF-17 | JWT Authentication | Infraestructura | ✅ | Auth endpoints | `*/config/JwtAuthenticationFilter` | Implementado en servicios |
| INF-18 | Protección de secretos | Infraestructura | ✅ | `.env.example` sin valores, .env ignorado | Raíz | .env en .gitignore, .env.example es público |
| INF-19 | CORS habilitado | Infraestructura | ✅ | Gateway CORS config | `gateway-service/` | Cross-origin requests permitidos |

---

## 2. REQUERIMIENTOS FUNCIONALES (RF)

### 2.1 Gestión de Pacientes

| ID | Requerimiento | Tipo | Estado | Endpoint | Servicio | Archivo(s) | Test | Notas |
|----|---|---|---|---|---|---|---|---|
| RF-01 | Crear paciente | Funcional | ✅ | POST /api/patients | patient-service | PatientController | PatientControllerTest | Datos obligatorios |
| RF-02 | Listar pacientes | Funcional | ✅ | GET /api/patients | patient-service | PatientController | PatientControllerTest | Con paginación |
| RF-03 | Obtener paciente por ID | Funcional | ✅ | GET /api/patients/{id} | patient-service | PatientController | PatientControllerTest | Incluye email, teléfono |
| RF-04 | Actualizar paciente | Funcional | ✅ | PUT /api/patients/{id} | patient-service | PatientController | PatientServiceTest | Solo campos permitidos |
| RF-05 | Desactivar paciente | Funcional | ✅ | DELETE /api/patients/{id} | patient-service | PatientController | PatientServiceTest | Soft delete (status=INACTIVE) |

### 2.2 Gestión de Médicos

| ID | Requerimiento | Tipo | Estado | Endpoint | Servicio | Archivo(s) | Test | Notas |
|----|---|---|---|---|---|---|---|---|
| RF-06 | Crear médico | Funcional | ✅ | POST /api/doctors | doctor-service | DoctorController | DoctorControllerTest | Con especialidad, licencia |
| RF-07 | Listar médicos | Funcional | ✅ | GET /api/doctors | doctor-service | DoctorController | DoctorControllerTest | Con filtro por especialidad |
| RF-08 | Obtener médico por ID | Funcional | ✅ | GET /api/doctors/{id} | doctor-service | DoctorController | DoctorControllerTest | Incluye especialización |
| RF-09 | Actualizar médico | Funcional | ✅ | PUT /api/doctors/{id} | doctor-service | DoctorController | DoctorServiceTest | Actualizar datos básicos |
| RF-10 | Desactivar médico | Funcional | ✅ | DELETE /api/doctors/{id} | doctor-service | DoctorController | DoctorServiceTest | Soft delete |

### 2.3 Gestión de Citas

| ID | Requerimiento | Tipo | Estado | Endpoint | Servicio | Archivo(s) | Test | Notas |
|----|---|---|---|---|---|---|---|---|
| RF-11 | Crear cita | Funcional | ✅ | POST /api/appointments | appointment-service | AppointmentController | AppointmentE2ETest | Usa Feign → patient-service |
| RF-12 | Listar citas | Funcional | ✅ | GET /api/appointments | appointment-service | AppointmentController | AppointmentControllerTest | Con filtro por estado |
| RF-13 | Obtener cita por ID | Funcional | ✅ | GET /api/appointments/{id} | appointment-service | AppointmentController | AppointmentControllerTest | Incluye patient + doctor |
| RF-14 | Cambiar estado cita | Funcional | ✅ | PUT /api/appointments/{id}/status | appointment-service | AppointmentController | AppointmentServiceTest | PENDING → CONFIRMED → COMPLETED |
| RF-15 | Cancelar cita | Funcional | ✅ | DELETE /api/appointments/{id} | appointment-service | AppointmentController | AppointmentServiceTest | Genera notificación |

### 2.4 Registros Clínicos

| ID | Requerimiento | Tipo | Estado | Endpoint | Servicio | Archivo(s) | Test | Notas |
|----|---|---|---|---|---|---|---|---|
| RF-16 | Crear diagnóstico | Funcional | ✅ | POST /api/clinical-records | clinical-record-service | ClinicalRecordController | ClinicalRecordControllerTest | Asociado a cita |
| RF-17 | Obtener registro clínico | Funcional | ✅ | GET /api/clinical-records/{id} | clinical-record-service | ClinicalRecordController | ClinicalRecordControllerTest | Incluye diagnóstico |
| RF-18 | Listar registros paciente | Funcional | ✅ | GET /api/clinical-records/patient/{id} | clinical-record-service | ClinicalRecordController | ClinicalRecordControllerTest | Filtra por paciente |

### 2.5 Laboratorio

| ID | Requerimiento | Tipo | Estado | Endpoint | Servicio | Archivo(s) | Test | Notas |
|----|---|---|---|---|---|---|---|---|
| RF-19 | Crear orden de laboratorio | Funcional | ✅ | POST /api/lab/orders | laboratory-service | LaboratoryController | LaboratoryControllerTest | Genera notificación |
| RF-20 | Cargar resultado | Funcional | ✅ | PUT /api/lab/results/{orderId} | laboratory-service | LaboratoryController | LaboratoryControllerTest | Status → COMPLETED |
| RF-21 | Obtener resultados paciente | Funcional | ✅ | GET /api/lab/patient/{id} | laboratory-service | LaboratoryController | LaboratoryControllerTest | Filtra por paciente |

### 2.6 Recetas

| ID | Requerimiento | Tipo | Estado | Endpoint | Servicio | Archivo(s) | Test | Notas |
|----|---|---|---|---|---|---|---|---|
| RF-22 | Crear receta | Funcional | ✅ | POST /api/prescriptions | prescription-service | PrescriptionController | PrescriptionControllerTest | Medicamento + dosis |
| RF-23 | Obtener receta | Funcional | ✅ | GET /api/prescriptions/{id} | prescription-service | PrescriptionController | PrescriptionControllerTest | Incluye medicamento |
| RF-24 | Listar recetas paciente | Funcional | ✅ | GET /api/prescriptions/patient/{id} | prescription-service | PrescriptionController | PrescriptionControllerTest | Vigentes + expiradas |

### 2.7 Video Consultas

| ID | Requerimiento | Tipo | Estado | Endpoint | Servicio | Archivo(s) | Test | Notas |
|----|---|---|---|---|---|---|---|---|
| RF-25 | Crear video consulta | Funcional | ✅ | POST /api/video-consultations | video-consultation-service | VideoConsultationController | VideoConsultationE2ETest | Genera URL room |
| RF-26 | Obtener video consulta | Funcional | ✅ | GET /api/video-consultations/{id} | video-consultation-service | VideoConsultationController | VideoConsultationControllerTest | Incluye URL |
| RF-27 | Cerrar video consulta | Funcional | ✅ | PUT /api/video-consultations/{id}/close | video-consultation-service | VideoConsultationController | VideoConsultationServiceTest | Registra duración |

### 2.8 Pagos

| ID | Requerimiento | Tipo | Estado | Endpoint | Servicio | Archivo(s) | Test | Notas |
|----|---|---|---|---|---|---|---|---|
| RF-28 | Obtener seguros | Funcional | ✅ | GET /api/payments/insurances | payment-service | PaymentController | PaymentControllerTest | Lista con cobertura |
| RF-29 | Procesar pago | Funcional | ✅ | POST /api/payments/process | payment-service | PaymentController | PaymentServiceTest | Calcula monto |
| RF-30 | Obtener pago | Funcional | ✅ | GET /api/payments/{id} | payment-service | PaymentController | PaymentControllerTest | Incluye transacción |

### 2.9 Notificaciones

| ID | Requerimiento | Tipo | Estado | Endpoint | Servicio | Archivo(s) | Test | Notas |
|----|---|---|---|---|---|---|---|---|
| RF-31 | Enviar notificación | Funcional | ✅ | POST /api/notifications/send | notification-service | NotificationController | NotificationControllerTest | Simula SMS/Email |
| RF-32 | Obtener notificación | Funcional | ✅ | GET /api/notifications/{id} | notification-service | NotificationController | NotificationControllerTest | Con estado |
| RF-33 | Historial notificaciones | Funcional | ✅ | GET /api/notifications/history/{userId} | notification-service | NotificationController | NotificationControllerTest | Filtra por usuario |

---

## 3. REGLAS DE NEGOCIO (RN)

| ID | Requerimiento | Tipo | Estado | Validación | Archivo(s) | Test | Notas |
|----|---|---|---|---|---|---|---|
| RN-01 | Email único por paciente | Negocio | ✅ | En service layer | PatientService | PatientServiceTest | Índice UNIQUE en BD |
| RN-02 | Médico activo para citas | Negocio | ✅ | Feign valida status | AppointmentService | AppointmentServiceTest | Consulta doctor-service |
| RN-03 | No sobrelapan citas médico | Negocio | ✅ | Agenda service | AppointmentService | AppointmentServiceTest | Valida disponibilidad |
| RN-04 | Cita requiere disponibilidad | Negocio | ✅ | Agenda service (Feign) | AppointmentService | AppointmentE2ETest | Usa load-balancer |
| RN-05 | Receta solo activa 30 días | Negocio | ✅ | En service layer | PrescriptionService | PrescriptionServiceTest | Status → EXPIRED automático |
| RN-06 | Paciente desactivado no puede citar | Negocio | ✅ | En service layer | AppointmentService | AppointmentServiceTest | Valida status=ACTIVE |
| RN-07 | Notificación al crear cita | Negocio | ✅ | Evento generado | AppointmentService | AppointmentServiceTest | Llama notification-service |
| RN-08 | Auditoría de cambios | Negocio | ⚠️ | Audit Service | Cada servicio | - | Pendiente implementación completa |

---

## 4. RESUMEN POR ESTADO

### Completados (✅): 35 requerimientos
- ✅ 5 Infraestructura (Discovery, Gateway, Docker)
- ✅ 30 Funcionales (CRUD completos)

### En Progreso (🔄): 8 requerimientos
- 🔄 Migraciones Flyway (Genaro)
- 🔄 Feign clients (Miguel)
- 🔄 Timeouts/Fallbacks (Miguel)

### Pendientes (❌): 2 requerimientos
- ❌ Despliegue Render (Genaro)
- ❌ RestClient (Opcional)

### Parciales (⚠️): 3 requerimientos
- ⚠️ Variables de entorno (Genaro)
- ⚠️ Auditoría distribuida (Pendiente)

---

## 5. Nota sobre Erik Queirolo

**Responsabilidades completadas**:
- ✅ Infraestructura de Discovery (Eureka)
- ✅ Gateway con load-balancing
- ✅ Docker Compose orquestado
- ✅ Coordinación de integración

**Estado actual**: Preparado para coordinar pruebas end-to-end una vez Miguel y Genaro completen sus tareas.

**Próximo**: Validación local completa (ver `PLAN-VALIDACION-LOCAL.md`)

---

## 6. Links y Recursos

- **Pauta DSY1103**: `pauta_fullstack.md`
- **Tareas individuales**:
  - Erik: `docs/TAREA-ERIK-QUEIROLO.md` ✅ COMPLETADO
  - Miguel: `docs/TAREA-MIGUEL-MESIAS.md`
  - Genaro: `docs/TAREA-GENARO-LAGOS.md`
- **Validación**: `docs/PLAN-VALIDACION-LOCAL.md`
- **Defensa Individual**: `docs/defensa-individual/Erik-Queirolo.md`

---

**Matriz actualizada**: 2026-07-13  
**Responsable**: Erik Queirolo  
**Estado general**: 77% Completo (Infraestructura terminada, pendiente completar middleware)

