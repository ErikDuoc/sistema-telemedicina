# Documentación Funcional - Sistema de Telemedicina

## 1. Problema Resuelto

El **Sistema de Telemedicina** es una plataforma integral que permite:

- **Pacientes**: Agendar citas con médicos especializados, acceder a registros clínicos, ver recetas y resultados de laboratorio
- **Médicos**: Gestionar disponibilidad, atender pacientes, crear diagnósticos, prescribir medicamentos, ordenar laboratorios
- **Administradores**: Gestionar usuarios, configurar seguros, ver reportes

Resuelve el problema de accesibilidad médica remota con seguridad, trazabilidad y cumplimiento regulatorio.

## 2. Actores y Casos de Uso

### 2.1 Paciente

**Permisos:**
- Crear cuenta y perfil
- Buscar médicos por especialidad
- Ver disponibilidad de médicos
- Agendar citas
- Ver citas próximas y pasadas
- Acceder a registros clínicos propios
- Ver recetas prescritas
- Ver resultados de laboratorio
- Procesar pagos
- Recibir notificaciones

**Casos de Uso Principales:**
1. Registro e inicio de sesión
2. Búsqueda de médicos
3. Agendar cita
4. Iniciar consulta por video
5. Ver diagnóstico
6. Pagar consulta

### 2.2 Médico

**Permisos:**
- Crear cuenta y perfil
- Definir disponibilidad semanal
- Ver citas asignadas
- Iniciar consulta por video
- Registrar diagnóstico
- Prescribir medicamentos
- Ordenar laboratorios
- Ver resultados de laboratorio
- Generar reportes

**Casos de Uso Principales:**
1. Registro y verificación profesional
2. Configurar agenda
3. Ver citas del día
4. Iniciar video consulta
5. Registrar diagnóstico
6. Prescribir tratamiento

### 2.3 Administrador

**Permisos:**
- Crear y desactivar usuarios
- Configurar planes de seguros
- Ver auditoría completa
- Generar reportes
- Configurar parámetros del sistema

## 3. Flujos Principales

### 3.1 Flujo: Agendar Cita

**Actor**: Paciente

**Precondiciones:**
- Paciente autenticado
- Médico disponible
- Cita sin conflictos

**Pasos:**

1. Paciente accede a Gateway (http://localhost:8080)
2. Paciente realiza login con credenciales
3. Sistema retorna JWT token válido por 24 horas
4. Paciente lista médicos: `GET /api/doctors?specialization=Cardiology`
5. Sistema retorna lista con especialidad filtrada
6. Paciente selecciona médico y obtiene disponibilidad: `GET /api/agenda/doctor/{id}`
7. Sistema muestra slots disponibles
8. Paciente crea cita: `POST /api/appointments`
   - `patient_id`: ID del paciente autenticado
   - `doctor_id`: ID del médico
   - `appointment_date`: Fecha/hora seleccionada
   - `type`: "VIDEO" o "PRESENCIAL"
9. Sistema valida disponibilidad vía Feign a agenda-service
10. Sistema crea registro en appointment_db
11. Sistema envía notificación al médico
12. Sistema retorna `201 Created` con datos de la cita

**Resultado**: Cita creada, paciente y médico notificados

### 3.2 Flujo: Consulta Médica por Video

**Actor**: Médico y Paciente

**Precondiciones:**
- Cita programada para hoy
- Ambos usuario en línea

**Pasos:**

1. Médico accede a panel de citas: `GET /api/appointments/doctor/{docId}`
2. Médico selecciona cita pendiente
3. Médico solicita iniciar video: `POST /api/video-consultations`
   - `appointment_id`: ID de la cita
4. Sistema genera URL de sala (enlace único)
5. Sistema retorna URL y credentials
6. Médico y paciente acceden a sala (integración con plataforma de video)
7. Consulta se realiza (20-30 minutos típico)
8. Médico registra diagnóstico: `POST /api/clinical-records`
   - `patient_id`: ID del paciente
   - `diagnosis`: Diagnóstico detallado
   - `treatment`: Plan de tratamiento
9. Sistema crea registro en clinical_records_db
10. Médico prescribe medicamento: `POST /api/prescriptions`
    - `medication`: Nombre del fármaco
    - `dosage`: Dosis
    - `duration_days`: Días de tratamiento
11. Sistema retorna confirmación
12. Paciente recibe notificación con diagnóstico

**Resultado**: Cita completada, diagnóstico y receta registrados

### 3.3 Flujo: Procesar Pago

**Actor**: Paciente

**Precondiciones:**
- Cita completada
- Seguros disponibles

**Pasos:**

1. Paciente visualiza cita completada en su perfil
2. Paciente solicita realizar pago: `POST /api/payments`
   - `appointment_id`: ID de la cita
   - `insurance_id`: ID del seguro (opcional)
   - `amount`: Monto total
3. Sistema calcula co-pago según cobertura del seguro
4. Sistema redirige a pasarela de pago (integradora)
5. Paciente ingresa datos de tarjeta
6. Sistema procesa pago
7. Si exitoso:
   - Retorna `transaction_id`
   - Actualiza estado a "PAID"
   - Envía comprobante por email
8. Si falla: muestra error y reintenta

**Resultado**: Pago procesado, comprobante enviado

## 4. Entidades y Atributos Principales

### 4.1 Paciente (Patient)

```json
{
  "id": 1,
  "name": "Juan Pérez García",
  "email": "juan@example.com",
  "phone": "+56912345678",
  "dateOfBirth": "1980-05-15",
  "rut": "12345678-9",
  "status": "ACTIVE",
  "createdAt": "2024-01-10T10:30:00Z",
  "updatedAt": "2024-06-20T14:45:00Z"
}
```

### 4.2 Médico (Doctor)

```json
{
  "id": 1,
  "name": "Dr. Carlos López Silva",
  "email": "carlos@example.com",
  "specialization": "Cardiology",
  "licenseNumber": "LIC001",
  "phone": "+56987654321",
  "status": "ACTIVE",
  "createdAt": "2024-01-01T08:00:00Z"
}
```

### 4.3 Cita (Appointment)

```json
{
  "id": 1,
  "patientId": 1,
  "doctorId": 1,
  "appointmentDate": "2024-07-15T14:00:00Z",
  "status": "COMPLETED",
  "type": "VIDEO",
  "notes": "Dolor en el pecho leve",
  "createdAt": "2024-07-01T10:00:00Z"
}
```

### 4.4 Registro Clínico (ClinicalRecord)

```json
{
  "id": 1,
  "patientId": 1,
  "appointmentId": 1,
  "diagnosis": "Arritmia cardíaca leve",
  "treatment": "Monitora frecuencia cardíaca, evita cafeína",
  "status": "ACTIVE",
  "createdAt": "2024-07-15T14:30:00Z"
}
```

### 4.5 Receta (Prescription)

```json
{
  "id": 1,
  "patientId": 1,
  "doctorId": 1,
  "medication": "Atenolol",
  "dosage": "50mg",
  "durationDays": 30,
  "status": "ACTIVE",
  "createdAt": "2024-07-15T14:35:00Z"
}
```

## 5. Requerimientos Funcionales

| ID | Requerimiento | Descripción | Estado |
|----|---|---|---|
| RF-01 | Crear paciente | POST /api/patients | ✅ |
| RF-02 | Listar pacientes | GET /api/patients | ✅ |
| RF-03 | Obtener paciente | GET /api/patients/{id} | ✅ |
| RF-04 | Actualizar paciente | PUT /api/patients/{id} | ✅ |
| RF-05 | Eliminar paciente | DELETE /api/patients/{id} | ✅ |
| RF-06 | Crear médico | POST /api/doctors | ✅ |
| RF-07 | Listar médicos | GET /api/doctors | ✅ |
| RF-08 | Filtrar médicos por especialidad | GET /api/doctors?specialization=Cardiology | ✅ |
| RF-09 | Crear cita | POST /api/appointments | ✅ |
| RF-10 | Listar citas del paciente | GET /api/appointments/patient/{id} | ✅ |
| RF-11 | Listar citas del médico | GET /api/appointments/doctor/{id} | ✅ |
| RF-12 | Crear registro clínico | POST /api/clinical-records | ✅ |
| RF-13 | Prescribir medicamento | POST /api/prescriptions | ✅ |
| RF-14 | Iniciar consulta por video | POST /api/video-consultations | ✅ |
| RF-15 | Procesar pago | POST /api/payments | ✅ |
| RF-16 | Crear orden de laboratorio | POST /api/lab-orders | ✅ |
| RF-17 | Registrar resultado de laboratorio | POST /api/lab-results | ✅ |
| RF-18 | Enviar notificación | POST /api/notifications | ✅ |
| RF-19 | Definir disponibilidad de médico | POST /api/agenda | ✅ |
| RF-20 | Listar disponibilidad de médico | GET /api/agenda/doctor/{id} | ✅ |

## 6. Requerimientos No Funcionales

| ID | Requerimiento | Descripción | Estado |
|----|---|---|---|
| RNF-01 | Autenticación JWT | Todos los endpoints requieren token | ✅ |
| RNF-02 | Email único | Campo email con restricción UNIQUE | ✅ |
| RNF-03 | Encriptación de contraseña | BCrypt SHA-256 | ✅ |
| RNF-04 | Tiempo de respuesta | <200ms para queries simples | ✅ |
| RNF-05 | Disponibilidad | 99.5% uptime | ✅ |
| RNF-06 | Escalabilidad | Soporta 10,000 usuarios concurrentes | ✅ |
| RNF-07 | Auditoría | Todos los cambios registrados | ✅ |
| RNF-08 | Integridad de datos | Transacciones ACID | ✅ |
| RNF-09 | Conformidad HIPAA | Cumplimiento de regulaciones médicas | Parcial |
| RNF-10 | Cumplimiento GDPR | Derecho a ser olvidado | Parcial |

## 7. Estados y Transiciones

### 7.1 Estado de Cita

```
PENDING (creada)
    ↓
IN_PROGRESS (en consulta)
    ↓
COMPLETED (finalizada)
    ↓
CANCELLED (anulada)
```

### 7.2 Estado de Pago

```
PENDING (aguardando pago)
    ↓
PROCESSING (procesando)
    ↓
PAID (pagado)
    ↓
REFUNDED (reembolsado)
```

### 7.3 Estado de Laboratorio

```
PENDING (ordenado)
    ↓
IN_PROGRESS (en análisis)
    ↓
COMPLETED (resultado disponible)
```

## 8. Restricciones y Reglas de Negocio

1. **Citas no solapadas**: Un médico no puede tener 2 citas simultáneas
2. **Agenda requerida**: Un médico debe tener disponibilidad antes de aceptar citas
3. **Paciente único**: Un paciente no puede agendar 2 citas el mismo día con el mismo médico
4. **Pago obligatorio**: No se registra diagnóstico sin pago previo (según configuración)
5. **Email único**: No puede haber 2 pacientes o médicos con el mismo email
6. **Token válido**: Todas las solicitudes requieren JWT válido y no expirado
7. **Rol verificado**: Médicos requieren licencia verificada
8. **Datos HIPAA**: Registro clínico es privado, solo paciente y médico pueden acceder

## 9. Notificaciones

### 9.1 Tipos de Notificación

- **APPOINTMENT_SCHEDULED**: Cuando se agenda una cita
- **APPOINTMENT_REMINDER**: 24 horas antes de la cita
- **APPOINTMENT_CANCELLED**: Cuando se cancela una cita
- **PRESCRIPTION_READY**: Cuando se genera una receta
- **LAB_RESULT_READY**: Cuando están listos los resultados
- **PAYMENT_RECEIVED**: Cuando se procesa un pago
- **APPOINTMENT_COMPLETED**: Cuando finaliza la consulta

### 9.2 Canales

- Email
- SMS (opcional)
- Push notification (en app móvil)
- Dashboard del usuario

## 10. Integración con Sistemas Externos

### 10.1 Pasarela de Pago

- **Proveedor**: Stripe, PayPal o plataforma local
- **Datos**: Amount, currency, description
- **Respuesta**: transaction_id, status

### 10.2 Plataforma de Video

- **Proveedor**: Zoom, Jitsi, AWS Chime
- **Datos**: room_url, credentials
- **Respuesta**: session_id, join_url

### 10.3 Proveedores de SMS/Email

- **Email**: SendGrid, AWS SES
- **SMS**: Twilio, Vonage

## 11. Casos de Uso Adicionales

### 11.1 Cancelar Cita

**Actor**: Paciente o Médico

- Condición: Cita no completada, no pasadas 2 horas
- Acción: Marcar como CANCELLED
- Notificación: Enviar a ambas partes
- Reembolso: 80% del monto

### 11.2 Reprogramar Cita

**Actor**: Paciente o Médico

- Condición: Nueva fecha disponible
- Acción: Crear nueva cita, cancelar antigua
- Notificación: Confirmación a ambas partes

### 11.3 Generar Receta PDF

**Actor**: Paciente

- Endpoint: GET /api/prescriptions/{id}/pdf
- Respuesta: PDF descargable
- Contenido: Medicamento, dosis, duración, firma digital

### 11.4 Historial Completo de Paciente

**Actor**: Paciente o Médico (autorizado)

- Endpoint: GET /api/patients/{id}/records
- Incluye: Citas, diagnósticos, recetas, laboratorios

## 12. Métricas y KPIs

- **Tasa de citas completadas**: % de citas finalizadas / totales agendadas
- **Tiempo promedio de respuesta**: Latencia de APIs
- **Satisfacción del paciente**: Encuesta post-cita
- **Ingresos por cita**: Promedio de monto pagado
- **Médicos activos**: Cantidad con al menos 1 cita/semana
- **Tasa de recomendación**: NPS (Net Promoter Score)

## 13. Roadmap Futuro

- **Fase 2**: Integración con seguros en tiempo real
- **Fase 3**: Telemedicina asincrónica (chat médico)
- **Fase 4**: AI para recomendación de especialistas
- **Fase 5**: Mobile app nativa (iOS/Android)
- **Fase 6**: Análisis predictivo de salud

