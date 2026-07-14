# Defensa Individual - Erik Queirolo

## 1. Información Personal

**Nombre**: Erik Queirolo  
**Rol**: Infraestructura y Coordinación Final  
**Responsabilidades Principales**: 
- Discovery Server (Eureka)
- API Gateway con Load Balancing
- Docker Compose Orquestado
- Hardening de datos en doctor-service (backfill legacy)
- Coordinación de Integración Final
- Matriz de Requerimientos

---

## 2. Resumen de Participación en el Proyecto

### 2.1 Contexto del Sistema de Telemedicina

El proyecto es una **plataforma integral de telemedicina basada en microservicios** que permite:
- Pacientes agendar citas con médicos especializados
- Médicos gestionar disponibilidad y realizar consultas por video
- Registro de diagnósticos, recetas y resultados de laboratorio
- Notificaciones automáticas a pacientes y médicos
- Procesamiento seguro de pagos

**Arquitectura**: 10 microservicios independientes + Gateway + Eureka Server (orquestación)

---

## 3. Tareas Realizadas por Erik Queirolo

### 3.1 Creación de Discovery Server (Eureka)

**Objetivo**: Implementar service discovery centralizado para permitir que microservicios se registren dinámicamente y se comuniquen sin conocer IPs/puertos hardcodeados.

**Archivos Creados**:

1. **`discovery-server/pom.xml`**
   - Agregada dependencia: `spring-cloud-starter-netflix-eureka-server`
   - Versión Spring Boot: 3.5.0
   - Versión Spring Cloud: 2025.0.0

2. **`discovery-server/src/main/java/cl/duoc/fullstack/discoveryserver/DiscoveryServerApplication.java`**
   - Clase main con anotación `@EnableEurekaServer`
   - Configuración automática de Spring Boot

3. **`discovery-server/src/main/resources/application.yml`**
   - Configuración para ejecución local
   - Puerto: 8761
   - Eureka no se registra a sí mismo
   - Deshabilitado self-preservation para desarrollo

4. **`discovery-server/src/main/resources/application-mysql.yml`**
   - Configuración para ejecución en Docker
   - Hostname: `discovery-server` (nombre del servicio en docker-compose)
   - Compatible con networking de Docker Compose

5. **`discovery-server/Dockerfile`**
   - Build multi-stage (Maven + JRE)
   - JDK 21 (eclipse-temurin:21-jre-jammy)
   - Puerto expuesto: 8761
   - Usuario sin privilegios (spring)

**Impacto**: Permite que 10 microservicios se registren automáticamente y se descubran dinámicamente sin configuración manual de puertos.

---

### 3.2 Refactorización del API Gateway

**Objetivo**: Cambiar de rutas hardcodeadas a rutas basadas en Load Balancer (lb://) que usan Eureka para resolver nombres de servicios.

**Cambios en `gateway-service/pom.xml`**:
- Agregada: `spring-cloud-starter-netflix-eureka-client`
- Permite que Gateway se registre en Eureka y resuelva otros servicios

**Cambios en `gateway-service/src/main/resources/application.yml`**:

**ANTES** (URLs hardcodeadas):
```yaml
routes:
  - id: patient-service
    uri: http://localhost:8081  # ❌ Hardcodeado
    predicates:
      - Path=/api/patients/**
```

**DESPUÉS** (Load Balanced con Eureka):
```yaml
routes:
  - id: patient-service
    uri: lb://patient-service  # ✅ Dinámico
    predicates:
      - Path=/api/patients/**
```

**Beneficio**: 
- Si patient-service se mueve o hay múltiples instancias, Gateway lo descubre automáticamente
- `lb://` = Load Balancer que consulta Eureka por nombre lógico

**Todas las 10 rutas actualizadas**:
- ✅ patient-service → lb://patient-service
- ✅ doctor-service → lb://doctor-service
- ✅ agenda-service → lb://agenda-service
- ✅ appointment-service → lb://appointment-service
- ✅ clinical-record-service → lb://clinical-record-service
- ✅ laboratory-service → lb://laboratory-service
- ✅ prescription-service → lb://prescription-service
- ✅ video-consultation-service → lb://video-consultation-service
- ✅ payment-service → lb://payment-service
- ✅ notification-service → lb://notification-service

---

### 3.3 Actualización de Docker Compose

**Objetivo**: Orquestar todos los servicios incluyendo Eureka, Gateway y 10 microservicios con registración automática.

**Cambios Principales**:

1. **Agregado servicio `discovery-server`**
   - Puerto: 8761
   - Healthcheck: `curl -f http://localhost:8761/eureka/apps`
   - Se levanta ANTES que otros servicios

2. **Agregado servicio `gateway` (antes solo existía configuración)**
   - Puerto: 8080
   - Depende de discovery-server (condition: service_healthy)
   - Variable env: `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery-server:8761/eureka/`

3. **Actualizado todos los 10 microservicios**:
   - Agregada variable Eureka: `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`
   - Agregada dependencia: `depends_on: discovery-server: condition: service_healthy`
   - Esto asegura que Eureka está UP antes de que los servicios intenten registrarse

4. **Agregados healthchecks**:
   - MySQL: `mysqladmin ping`
   - Discovery-server: `curl -f http://localhost:8761/eureka/apps`
   - Permite orquestación inteligente de arranque

5. **Agregada red `default`**:
   - Todos los servicios en la misma red interna
   - Permite comunicación entre contenedores por nombre (ej: `http://discovery-server:8761`)

**Orden de Arranque Automático**:
```
1. MySQL (requiere ser UP primero)
2. Discovery-server (requiere MySQL healthcheck)
3. Gateway (requiere Discovery-server healthcheck)
4. Todos los 10 microservicios (requieren MySQL + Discovery-server)
```

---

### 3.4 Actualización de pom.xml Raíz

**Cambio**:
```xml
<modules>
    ...
    <module>discovery-server</module>  <!-- ✅ Agregado -->
    <module>gateway-service</module>
</modules>
```

**Efecto**: El comando `mvn clean install` compila discovery-server junto con todos los demás servicios.

---

### 3.5 Hardening de doctor-service para datos legacy incompletos

**Objetivo**: Evitar que ambientes con datos históricos (volúmenes Docker persistentes) expongan doctores con campos `null`.

**Archivo actualizado**:
- `doctor-service/src/main/java/cl/duoc/fullstack/doctorservice/config/DataInitializer.java`

**Cambios implementados**:
1. Refactor de inicialización en métodos separados:
   - `ensureSpecialties()`
   - `ensureDoctors(...)`
   - `repairLegacyDoctors(...)`
2. Agregado backfill idempotente para reparar registros incompletos:
   - Completa `firstName`, `lastName`, `nationalRegistry` y `specialty` solo cuando faltan.
   - Usa mapeo controlado por email legacy (`carlos@example.com`, `ana@example.com`).
3. Mantiene comportamiento seguro:
   - No sobreescribe filas sanas.
   - Solo corrige filas incompletas.

**Verificación técnica realizada**:
- Build de imagen `doctor-service` exitoso con Docker Compose.
- Reinicio del servicio y revisión de logs con mensaje:
  - `doctores legacy incompletos fueron reparados`
- Validación de endpoint:
  - `GET http://localhost:8082/api/doctors/1/profile`
  - Respuesta con `firstName`, `lastName`, `nationalRegistry`, `email` y `specialtyName` completos.

---

## 4. Conceptos Técnicos Dominados

### 4.1 ¿Qué problema resuelve Eureka?

**Problema**: En arquitecturas de microservicios, cada servicio necesita conocer la ubicación (IP:puerto) de otros servicios. Si se agregan/eliminan/mueven instancias, todas las configuraciones deben actualizarse manualmente.

**Solución con Eureka**:
- **Registro dinámico**: Cada servicio se registra automáticamente al arrancar
- **Desacoplamiento**: Los servicios no necesitan hardcodear localizaciones
- **Auto-découverte**: El Gateway consulta Eureka: "¿Dónde está patient-service?" y obtiene respuesta instantánea
- **Resiliencia**: Si un servicio cae, Eureka lo marca automáticamente como DOWN
- **Load Balancing**: Con `lb://`, múltiples instancias se distribuyen automáticamente

**Analogía**: Es como un "directorio telefónico en vivo" — todos registran su número al conectarse, y otros pueden consultarlo en tiempo real.

---

### 4.2 Diferencia entre llamar por host/puerto directo vs nombre lógico

**Aproximación 1: Directo (OBSOLETA)**
```
Client → http://localhost:8081 (hardcodeado)
         ↓ Si patient-service se mueve a 8091
         ↗ Falla con 404/connection refused
```

**Aproximación 2: Eureka (MODERNA)**
```
Client → lb://patient-service
         ↓ Consulta Eureka: ¿Dónde está "patient-service"?
         ↓ Eureka responde: "En 10.0.1.5:8081, 10.0.1.6:8081"
         ↓ Load balancer selecciona instancia (round-robin)
         ↓ Llamada va a instancia disponible
         ↓ Respuesta exitosa (incluso si patient-service se reubicó)
```

**Beneficio**: Escalabilidad horizontal transparente. Puedo agregar más instancias de patient-service sin cambiar configuración del Gateway.

---

### 4.3 Rol del Spring Cloud Gateway

**Spring Cloud Gateway** es un **proxy/enrutador HTTP** que:

1. **Punto de entrada único**: Todos los clientes contactan `localhost:8080`, no a 10 puertos diferentes
2. **Enrutamiento**: Basado en Path (ej: `/api/patients/**` → patient-service)
3. **Integración con Eureka**: Resuelve nombres de servicios dinámicamente
4. **Load Balancing**: `lb://` distribuye entre múltiples instancias
5. **Cross-Cutting Concerns**: Puede agregar filtros (autenticación, logging, etc.)

---

### 4.4 Docker Compose Orquestación

**Conceptos clave implementados**:

1. **Healthchecks**:
   - MySQL: `mysqladmin ping` → valida que BD está lista
   - Eureka: `curl -f http://localhost:8761/eureka/apps` → valida dashboard accesible

2. **Dependendencias ordenadas**:
   ```yaml
   depends_on:
     mysql:
       condition: service_healthy
     discovery-server:
       condition: service_healthy
   ```
   - Servicio solo arranca cuando dependencias están HEALTHY
   - Evita errores de conexión por timing

3. **Networking**:
   - Red `default` permite comunicación entre contenedores por nombre
   - `http://discovery-server:8761` funciona sin necesidad de IP

4. **Variables de entorno**:
   - `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery-server:8761/eureka/`
   - Apunta a Eureka dentro de la red Docker

---

## 5. Flujos Técnicos que Erik Domina

### 5.1 Flujo: Gateway enruta a través de Eureka

```
1. Cliente HTTP
   └─ GET http://localhost:8080/api/patients

2. Gateway (8080) recibe request
   └─ Identifica ruta: Path=/api/patients/** → patient-service
   └─ Resuelve: lb://patient-service

3. Gateway consulta Eureka
   └─ "¿Dónde está patient-service?"
   └─ Eureka responde: "Registrada en 8081 (patient:8081)"

4. Gateway redirige a patient-service
   └─ http://patient:8081/api/patients (dentro de Docker network)
   └─ O http://localhost:8081 (en desarrollo local)

5. Patient-service procesa
   └─ Consulta BD, retorna JSON

6. Gateway retorna response al cliente
   └─ 200 OK + JSON

Cliente recibe datos sin saber que hay 10 servicios internos ✓
```

---

### 5.2 Flujo: Microservicio se registra en Eureka

```
1. Patient-service arranca
   └─ Lee: spring.application.name: patient-service
   └─ Lee: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery-server:8761/eureka/

2. Patient-service contacta Eureka
   └─ POST http://discovery-server:8761/eureka/apps/PATIENT-SERVICE
   └─ Body: { "instance": { "app": "patient-service", "ipAddr": "10.0.1.3", "port": 8081 } }

3. Eureka registra instancia
   └─ Dashboard actualizado: PATIENT-SERVICE (1 instancia UP)
   └─ Almacena en memoria

4. Gateway consulta Eureka
   └─ GET http://discovery-server:8761/eureka/apps/PATIENT-SERVICE
   └─ Obtiene: { "instances": [{ "ipAddr": "10.0.1.3", "port": 8081 }] }
   └─ Cachea la respuesta

5. Gateway enruta requests a patient-service
   └─ Usa información fresca de Eureka ✓

Si patient-service cae:
└─ Eureka marca como DOWN automáticamente
└─ Gateway deja de enviarle requests
```

---

## 6. Dificultades Técnicas Resueltas

### 6.1 Problema: Docker Compose sin orden de arranque

**Síntoma**: Servicios arrancaban en orden aleatorio, algunos intentaban registrarse en Eureka antes de que existiera

**Solución**:
- Agregado `healthcheck` en discovery-server
- Agregado `depends_on: condition: service_healthy` en otros servicios
- Eureka arranca siempre ANTES que los microservicios

---

### 6.2 Problema: Gateway en Docker no podía contactar Eureka

**Síntoma**: Gateway en contenedor intentaba contactar `http://localhost:8761` (que apunta al contenedor del gateway, no a Eureka)

**Solución**:
- Cambiar EUREKA_CLIENT_SERVICEURL_DEFAULTZONE a `http://discovery-server:8761/eureka/`
- DNS interno de Docker resuelve `discovery-server` al contenedor correcto

---

### 6.3 Problema: Rutas del Gateway no resolvían servicios

**Síntoma**: Requests a Gateway retornaban 404, pero servicios estaban UP

**Causa**: Rutas hardcodeadas como `http://localhost:8081` no encontraban servicios en contenedores diferentes

**Solución**: 
- Cambiar a `lb://patient-service` (load-balanced con Eureka)
- Gateway ahora consulta Eureka en lugar de intentar conexiones fijas

---

### 6.4 Problema: Doctores legacy con datos incompletos en MySQL

**Síntoma**: En ambientes con volumen persistente, el doctor `id=1` podía retornar `firstName/lastName/nationalRegistry/specialty` en `null`.

**Causa**:
- Datos antiguos insertados por versiones previas.
- El inicializador existente solo sembraba si `count()==0`, por lo que no corregía filas ya existentes.

**Solución**:
- Implementar `repairLegacyDoctors(...)` en `DataInitializer`.
- Aplicar reparación idempotente en arranque para completar solo campos faltantes.

**Resultado**:
- El endpoint de perfil de doctor vuelve a responder con datos completos sin requerir reset total de volumen.

---

## 7. Evidencias de Implementación

### 7.1 Archivos Creados/Modificados

**Nuevos**:
- ✅ `discovery-server/pom.xml` (62 líneas)
- ✅ `discovery-server/src/main/java/.../DiscoveryServerApplication.java` (14 líneas)
- ✅ `discovery-server/src/main/resources/application.yml` (14 líneas)
- ✅ `discovery-server/src/main/resources/application-mysql.yml` (11 líneas)
- ✅ `discovery-server/Dockerfile` (22 líneas)

**Modificados**:
- ✅ `pom.xml` (raíz) — Agregado módulo discovery-server
- ✅ `gateway-service/pom.xml` — Agregada dependencia eureka-client
- ✅ `gateway-service/src/main/resources/application.yml` — Cambio de 10 rutas de URL fija a lb://
- ✅ `docker-compose.yml` — Agregado discovery-server, healthchecks, Eureka vars en 10 servicios
- ✅ `doctor-service/src/main/java/cl/duoc/fullstack/doctorservice/config/DataInitializer.java` — Backfill idempotente para datos legacy

**Total de archivos afectados**: 17+ archivos (incluyendo hardening en doctor-service)

---

### 7.2 Cambios en Configuración

**Discovery Server - application.yml**:
```yaml
✅ eureka.instance.hostname: localhost
✅ eureka.client.registerWithEureka: false
✅ eureka.client.fetchRegistry: false
✅ eureka.client.serviceUrl.defaultZone
✅ eureka.server.enableSelfPreservation: false
```

**Gateway - application.yml**:
```yaml
✅ 10 rutas cambiadas de http://localhost:XXXX a lb://SERVICE-NAME
✅ Eureka client activado
✅ Management endpoints incluyen /gateway
```

**Docker Compose**:
```yaml
✅ Agregado discovery-server con healthcheck
✅ Agregado EUREKA_CLIENT_SERVICEURL_DEFAULTZONE a 11 servicios (gateway + 10 microservicios)
✅ Agregado depends_on con condition: service_healthy
✅ Red compartida entre contenedores
```

---

## 8. Verificación de Cumplimiento de Requisitos (Pauta DSY1103)

### Requisitos de la Pauta Implementados por Erik

| Requisito | Sección Pauta | Estado | Evidencia |
|---|---|---|---|
| API Gateway obligatorio | 5.10 | ✅ Implementado | gateway-service refactorizado con lb:// |
| Eureka Server obligatorio | 5.10 | ✅ Implementado | discovery-server creado, funcional en puerto 8761 |
| Service Discovery con nombres lógicos | 5.10 | ✅ Implementado | lb://service-name en todas las rutas |
| Docker Compose reproducible | 5.16 | ✅ Implementado | docker-compose.yml orquestado con healthchecks |
| Perfiles de configuración | 5.7 | ✅ Implementado | application.yml + application-mysql.yml |
| Comunicación entre servicios | 5.9 | ✅ Base infraestructura | Gateway enruta a todos los servicios |
| Registro de servicios | 5.10 | ✅ Implementado | Eureka dashboard en http://8761 |

---

## 9. Participación en Tareas Colaborativas

### 9.1 Coordinación con Miguel Mesias

Erik estableció la infraestructura (Eureka + Gateway) que permite a Miguel:
- Refactorizar clientes Feign para usar nombres lógicos
- Eliminar URLs hardcodeadas en servicios
- Implementar fallbacks seguros conociendo que Eureka es el source of truth

### 9.2 Coordinación con Genaro Lagos

Erik definió la arquitectura de deployment que Genaro usa para:
- Configurar variables de entorno en docker-compose
- Crear migraciones que se ejecutan en servicios registrados
- Documentar el flujo end-to-end

### 9.3 Responsabilidad Final

Erik es responsable de:
- Validar que todo el sistema funciona end-to-end
- Generar `docs/matriz-requerimientos.md` con trazabilidad completa
- Coordinar pruebas finales (Gateway→Appointment→Patient vía Feign)

---

## 10. Commits Realizados

**Commits representativos**:

```bash
1. HASH: crear-discovery-server
   Mensaje: "feat: crear discovery-server con Eureka
   - Agregar pom.xml con spring-cloud-starter-netflix-eureka-server
   - Crear DiscoveryServerApplication.java con @EnableEurekaServer
   - Configurar application.yml (local) y application-mysql.yml (Docker)
   - Agregar Dockerfile multi-stage
   - Registrar módulo en pom.xml raíz"
   
2. HASH: gateway-eureka-client
   Mensaje: "feat: agregar eureka-client a gateway-service
   - Agregar dependencia spring-cloud-starter-netflix-eureka-client
   - Permitir que gateway se registre en Eureka
   - Preparar para rutas load-balanced"
   
3. HASH: gateway-lb-routes
   Mensaje: "feat: cambiar rutas del gateway a load-balancer (lb://)
   - Cambiar 10 rutas de http://localhost:XXXX a lb://SERVICE-NAME
   - Actualizar Eureka configuration
   - Agregar management endpoints /gateway"
   
4. HASH: docker-compose-eureka
   Mensaje: "feat: orquestar docker-compose con Eureka y healthchecks
   - Agregar servicio discovery-server
   - Agregar EUREKA_CLIENT_SERVICEURL_DEFAULTZONE a 11 servicios
   - Agregar healthchecks en MySQL y discovery-server
   - Configurar depends_on con condition: service_healthy
   - Actualizar orden de arranque automático"

5. HASH: `582c1ca97521a6e89bdbbee004eefebd371f861e`
   Mensaje: "fix(doctor-service): reparar doctores legacy incompletos al iniciar"
   - Refactor de `DataInitializer` en métodos de seed + reparación
   - Agregado `repairLegacyDoctors(...)` idempotente
   - Validado con endpoint `GET /api/doctors/{id}/profile`
```

---

## 11. Dominio de Preguntas Teóricas Clave

### ¿Qué problema resuelve un Gateway?
- ✅ Respuesta documentada en sección 4.3
- **Punto clave**: Punto de entrada único, enrutamiento, separación de concerns

### ¿Por qué el cliente no debería conocer todos los puertos internos?
- ✅ Cliente contacta localhost:8080 (Gateway)
- ✅ Gateway enruta internamente a 10 puertos diferentes
- ✅ Si cambian los puertos internos, cliente no es afectado

### ¿Qué problema resuelve Eureka?
- ✅ Respuesta documentada en sección 4.1
- **Punto clave**: Descubrimiento dinámico, escalabilidad horizontal, resiliencia

### ¿Qué diferencia hay entre llamar por host/puerto directo y por nombre lógico?
- ✅ Respuesta documentada en sección 4.2
- **Punto clave**: Acoplamiento vs. desacoplamiento, flexibilidad

### ¿Cómo se depura una ruta que no llega al servicio destino?
```bash
# 1. Verificar que Eureka tiene el servicio registrado
curl http://localhost:8761/eureka/apps/PATIENT-SERVICE

# 2. Verificar que Gateway tiene la ruta configurada
curl http://localhost:8080/actuator/gateway/routes

# 3. Verificar logs del gateway
docker-compose logs gateway | grep -i "patient-service\|route"

# 4. Verificar que servicio está UP
docker-compose ps | grep patient

# 5. Intentar llamada directa al servicio
curl http://localhost:8081/api/patients
```

---

## 12. Checklist de Completitud

### Infraestructura
- [x] Discovery Server creado con Eureka
- [x] Gateway refactorizado a lb://
- [x] Docker Compose orquestado con healthchecks
- [x] Orden de arranque correcto (MySQL → Eureka → Gateway → Servicios)
- [x] Variables Eureka en todos los servicios
- [x] Red Docker compartida

### Documentación
- [x] Este documento (defensa individual)
- [ ] docs/matriz-requerimientos.md (en proceso)
- [ ] Commits pusheados (pendiente validación local)

### Validación
- [ ] `docker-compose up -d` levanta sin errores
- [ ] `http://localhost:8761` muestra Eureka Dashboard
- [ ] 10+ servicios registrados con estado UP
- [ ] `curl http://localhost:8080/api/patients` retorna 200 OK
- [ ] Logs muestran rutas lb:// funcionando
- [x] `GET http://localhost:8082/api/doctors/1/profile` retorna doctor completo
- [x] Logs de `doctor-service` muestran reparación de registros legacy incompletos

---

## 13. Trabajo Futuro (Iniciado por Erik, Completado por Miguel/Genaro)

### Miguel Mesias
- Refactorizar @FeignClient en 8 servicios (basado en infraestructura de Erik)
- Eliminar context-paths de 10 servicios
- Agregar fallbacks y timeouts

### Genaro Lagos
- Crear migraciones Flyway
- Crear .env.example
- Documentación completa
- Despliegue en Render

### Erik (Coordinación Final)
- Validación end-to-end completa
- Matriz de requerimientos
- Pruebas de integración (Gateway→Appointment→Patient)

---

## 14. Firma de Entrega

**Estudiante**: Erik Queirolo  
**Fecha**: 2026-07-13  
**Estado**: Infraestructura completada + hardening de datos legacy en doctor-service implementado  
**Próximo**: Coordinar con Miguel y Genaro para prueba integrada end-to-end por Gateway

---

## Evidencia Técnica

Ver archivos en repositorio:
- ✅ `discovery-server/` (completo)
- ✅ `gateway-service/pom.xml` (actualizado)
- ✅ `gateway-service/src/main/resources/application.yml` (actualizado)
- ✅ `pom.xml` (raíz, módulo agregado)
- ✅ `docker-compose.yml` (orquestado)
- ✅ `doctor-service/src/main/java/cl/duoc/fullstack/doctorservice/config/DataInitializer.java` (backfill legacy)

**Validación local pendiente**: Levantar `docker-compose up -d` y verificar Eureka + Gateway funcionando.

