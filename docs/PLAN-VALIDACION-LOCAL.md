# Plan de Validación Local - Sistema de Telemedicina

**Fecha**: 2026-07-13  
**Responsable**: Erik Queirolo  
**Objetivo**: Validar que Eureka + Gateway + Docker Compose funcionan correctamente

---

## PASO 4.1: Pre-requisitos

Asegúrate que tienes:
- ✅ Docker Desktop instalado y ejecutándose
- ✅ Git configurado (para commits)
- ✅ Archivos del proyecto descargados

---

## PASO 4.2: Levantar Docker Compose

Desde la raíz del proyecto:

```bash
# 1. Navegar a la carpeta del proyecto
cd "E:/Estudio DUOC/Semestre 3/sistema-telemedicina"

# 2. Detener cualquier instancia anterior (por si acaso)
docker-compose down -v

# 3. Levantar servicios
docker-compose up -d

# 4. Esperar ~2 minutos (primeras compilaciones y arranques son lentos)
# Los logs mostrarán cuando cada servicio esté listo
```

---

## PASO 4.3: Verificar Eureka Dashboard

**URL**: `http://localhost:8761`

### Checklist:

```
✅ Dashboard visible (página HTML cargada)
✅ Sección "Instances currently registered with Eureka"
✅ Mínimo 10 servicios listados:
   - PATIENT-SERVICE (1 instancia UP)
   - DOCTOR-SERVICE (1 instancia UP)
   - AGENDA-SERVICE (1 instancia UP)
   - APPOINTMENT-SERVICE (1 instancia UP)
   - CLINICAL-RECORD-SERVICE (1 instancia UP)
   - LABORATORY-SERVICE (1 instancia UP)
   - PRESCRIPTION-SERVICE (1 instancia UP)
   - VIDEO-CONSULTATION-SERVICE (1 instancia UP)
   - PAYMENT-SERVICE (1 instancia UP)
   - NOTIFICATION-SERVICE (1 instancia UP)
✅ Cada instancia muestra status "UP" (verde/disponible)
✅ GATEWAY-SERVICE registrado (opcional pero recomendado)
```

**Si ves esto**: ✅ Eureka está funcionando correctamente

**Si NO ves servicios**:
```bash
# Verificar logs de un servicio
docker-compose logs patient | grep -i "eureka\|registered"

# Debe mostrar algo como:
# "Successfully registered instance ... with eureka server"
```

---

## PASO 4.4: Verificar Gateway Enrutando

### Prueba 1: Health Check del Gateway

```bash
curl -X GET http://localhost:8080/actuator/health
```

**Resultado esperado**:
```json
{
  "status": "UP"
}
```

---

### Prueba 2: Obtener Rutas Configuradas

```bash
curl -X GET http://localhost:8080/actuator/gateway/routes
```

**Resultado esperado** (JSON con 10 rutas):
```json
[
  {
    "route_id": "patient-service",
    "predicates": [...],
    "uri": "lb://patient-service",  // ✅ Load-balanced
    ...
  },
  {
    "route_id": "doctor-service",
    "uri": "lb://doctor-service",
    ...
  },
  ...
]
```

**Verificar**: Todas las URIs deben ser `lb://SERVICE-NAME`, NO `http://localhost:XXXX`

---

### Prueba 3: Llamada Real a través del Gateway

```bash
# Obtener lista de pacientes a través del Gateway
curl -X GET http://localhost:8080/api/patients

# Resultado esperado:
# - 200 OK + JSON con pacientes (si existen datos)
# - O 200 OK + [] (lista vacía, pero sin 404)
# - NO debe ser 404 (que indicaría que Gateway no enrutó)

# Si es error de autorización (401), significa que la ruta funcionó
# pero falta autenticación, lo cual es correcto en seguridad
```

---

## PASO 4.5: Verificar Comunicación Feign (Preparation para Miguel)

### Prueba: Crear una cita (requiere Feign llamando a Patient)

```bash
# Primero crear un paciente
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Patient",
    "email": "test@example.com",
    "phone": "+56912345678",
    "password": "password123"
  }'

# Luego crear una cita (esto usará Feign internamente para validar paciente)
curl -X POST http://localhost:8080/api/appointments \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 1,
    "doctorId": 1,
    "appointmentDate": "2026-07-20T10:00:00",
    "type": "CONSULTATION"
  }'

# Si ambos retornan 201 Created o 200 OK, significa:
# ✅ Gateway enrutó correctamente
# ✅ Feign puede llamar a otros servicios via Eureka
# ✅ Todo está integrado
```

---

## PASO 4.6: Verificar Logs de Registración

```bash
# Ver logs de discovery-server
docker-compose logs discovery-server | grep -E "registered|heartbeat|deregistered" | head -20

# Ver logs de patient-service registrándose
docker-compose logs patient | grep -E "Registering instance|Successfully registered" | head -5

# Ver logs de gateway descubriendo servicios
docker-compose logs gateway | grep -E "LoadBalancerClientFactory|instance"| head -10
```

**Resultado esperado**: Logs mostrando que servicios se registran en Eureka sin errores.

---

## PASO 4.7: Detener Servicios

Cuando termines validación:

```bash
# Pausar (no elimina volúmenes/datos)
docker-compose stop

# O eliminar completamente
docker-compose down

# O eliminar TODO incluyendo volúmenes (limpieza completa)
docker-compose down -v
```

---

## Troubleshooting

### Problema: "Connection refused" en `http://localhost:8761`

**Causa**: Discovery-server no está arrancado

**Solución**:
```bash
docker-compose logs discovery-server | tail -20
# Ver qué error hay en los logs

# Verificar que contenedor está corriendo
docker ps | grep discovery

# Si no está, ver por qué falló
docker-compose up discovery-server
```

---

### Problema: Eureka Dashboard vacío (0 instancias)

**Causa**: Servicios no pueden registrarse (probablemente red/DNS)

**Solución**:
```bash
# Verificar que services pueden comunicarse
docker exec patient ping discovery-server

# Ver logs detallados
docker-compose logs patient 2>&1 | grep -i "eureka" | head -20

# Posible: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE es incorrecto
# Verificar docker-compose.yml línea ~65 en cada servicio
```

---

### Problema: Gateway retorna 404 para `/api/patients`

**Causa**: Ruta no está configurada o servicio no responde

**Solución**:
```bash
# Verificar rutas
curl http://localhost:8080/actuator/gateway/routes | grep patient

# Debe haber una ruta con:
# "route_id": "patient-service"
# "uri": "lb://patient-service"

# Si no está, problema en gateway/application.yml

# Si está pero 404, el servicio patient no responde:
curl http://localhost:8081/api/patients
# Si esto tampoco funciona, patient-service no está arrancado

docker ps | grep patient
```

---

### Problema: Servicios se reinician constantemente

**Causa**: Probablemente falla de inicialización

**Solución**:
```bash
# Ver logs del servicio que falla
docker-compose logs appointment | tail -50

# Buscar línea de error
# Común: "Cannot connect to Eureka" → verificar discovery-server
# Común: "Cannot connect to MySQL" → verificar mysql healthcheck
# Común: "Port already in use" → docker-compose down -v y reintentar
```

---

## Checklist Final de Validación

Después de levantar servicios, verificar TODOS estos puntos:

```
✅ Eureka Dashboard accesible (http://8761)
✅ 10+ servicios registrados
✅ Todos con status UP (verde)
✅ Gateway Health Check retorna "UP"
✅ Gateway rutas muestran lb://
✅ Llamada GET /api/patients no retorna 404
✅ MySQL está UP (docker-compose ps muestra mysql "Up")
✅ Logs sin mensajes de ERROR críticos
✅ No hay servicios en estado "Restarting"
```

**Si todos los puntos están ✅**: ¡Sistema completamente validado!

---

## Próximos Pasos

Una vez validado localmente:

1. **Miguel**: Puede proceder con refactor de Feign clients
2. **Genaro**: Puede proceder con migraciones Flyway
3. **Erik**: Crear commits, documentar matriz de requerimientos, preparar presentación

---

## Notas Importantes

- **Primer arranque lento**: Docker necesita descargar imágenes y compilar servicios
- **Logs útiles**: `docker-compose logs -f SERVICIO_NAME` para ver logs en tiempo real
- **Red Docker**: Todos los servicios están en la misma red interna, pueden comunicarse por nombre
- **Ports**: Gateway en 8080, Eureka en 8761, servicios en 8081-8091
- **Variables env**: Verificar que EUREKA_CLIENT_SERVICEURL_DEFAULTZONE está correcto en docker-compose.yml

---

**Fin del plan de validación**

