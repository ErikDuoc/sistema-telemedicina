# 🏥 PLATAFORMA DE TELEMEDICINA - MICROSERVICIOS

## Arquitectura Profesional de Microservicios (v1.0.0)
Sistema de gestión hospitalaria integrado con autenticación JWT centralizada, auditoría distribuida y resiliencia automática.

---

## 📋 ÍNDICE

1. [Inicio Rápido](#inicio-rápido)
2. [Arquitectura](#arquitectura)
3. [Configuración](#configuración)
4. [Seguridad](#seguridad)
5. [Despliegue](#despliegue)
6. [Troubleshooting](#troubleshooting)
7. [Documentación](#documentación)

---

## 🚀 INICIO RÁPIDO

### Requisitos
- Java 21+
- Maven 3.9+
- MySQL 8.0+ (opcional, H2 funciona para desarrollo)
- Git

### Desarrollo Local

1. **Clonar repositorio**
```bash
git clone <repositorio>
cd telemedicina-system
```

2. **Copiar configuración de desarrollo**
```bash
cp .env.development.example .env.local
```

3. **Construir proyecto**
```bash
mvn clean install
```

4. **Iniciar servicios** (en diferentes terminales, en este orden)
```bash
# Terminal 1: Audit Service
mvn spring-boot:run -f audit-service/pom.xml

# Terminal 2: Gateway Service  
mvn spring-boot:run -f gateway-service/pom.xml

# Terminal 3-12: Otros servicios (en paralelo)
mvn spring-boot:run -f patient-service/pom.xml
mvn spring-boot:run -f doctor-service/pom.xml
mvn spring-boot:run -f appointment-service/pom.xml
mvn spring-boot:run -f clinical-record-service/pom.xml
mvn spring-boot:run -f payment-service/pom.xml
mvn spring-boot:run -f agenda-service/pom.xml
mvn spring-boot:run -f laboratory-service/pom.xml
mvn spring-boot:run -f notification-service/pom.xml
mvn spring-boot:run -f prescription-service/pom.xml
mvn spring-boot:run -f video-consultation-service/pom.xml
```

5. **Verificar estado**
```bash
# Swagger UI activo:
- http://localhost:8080/swagger-ui.html (Gateway)
- http://localhost:8081/swagger-ui.html (Patient Service)
- http://localhost:8092/swagger-ui.html (Audit Service)

# H2 Console (desarrollo):
- http://localhost:8081/h2-console
- http://localhost:8092/h2-console
```

---

## 🏛️ ARQUITECTURA

### Servicios Desplegados

```
┌─────────────────────┐
│   CLIENTE (WEB/MOV) │
└──────────┬──────────┘
           │ HTTPS + JWT
           ▼
┌─────────────────────┐
│  GATEWAY SERVICE    │ :8080
│ (Spring Cloud GW)   │
│ - JWT Validation    │
│ - CORS Global       │
│ - Routing           │
└──────────┬──────────┘
           │
    ┌──────┴──────┬────────────┬────────────┬────────────┬──────────┐
    ▼             ▼            ▼            ▼            ▼          ▼
 PATIENT      DOCTOR      APPOINTMENT  CLINICAL    PAYMENT     AUDIT
 SERVICE     SERVICE       SERVICE     RECORD      SERVICE     SERVICE
  :8081       :8082         :8087      SERVICE      :8084       :8092
              │                        :8088          │
              │                                       │
              ▼                                       ▼
           AGENDA        NOTIFICATION    PHARMACY   (fallback)
           SERVICE       SERVICE         SERVICE
           :8085         :8083           :8090
```

### Puertos Asignados

| Servicio | Puerto | Descripción |
|----------|--------|------------|
| Gateway Service | 8080 | API Gateway centralizado |
| Patient Service | 8081 | Gestión de pacientes |
| Doctor Service | 8082 | Catálogo de médicos |
| Notification Service | 8083 | Alertas por email/SMS |
| Payment Service | 8084 | Procesamiento de pagos |
| Agenda Service | 8085 | Disponibilidad de citas |
| Laboratory Service | 8086 | Órdenes y resultados |
| Appointment Service | 8087 | Reservas de citas |
| Clinical Record Service | 8088 | Ficha clínica digital |
| Prescription Service | 8089 | Recetas electrónicas |
| Video Consultation Service | 8091 | Teleconsultas |
| Audit Service | 8092 | Auditoría centralizada |

---

## ⚙️ CONFIGURACIÓN

### Variables de Entorno (Desarrollo)
Copiar `.env.development.example` a `.env.local`:
```bash
JWT_SECRET=dev-secret-key-change-in-prod
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200,http://localhost:8080
```

### Variables de Entorno (Producción)
Crear `.env.production` con valores seguros:
```bash
JWT_SECRET=your-super-secure-random-secret-min-32-chars
JWT_EXPIRATION=86400000
CORS_ALLOWED_ORIGINS=https://tudominio.com,https://app.tudominio.com
DB_URL=jdbc:mysql://db-host:3306/db_prod
DB_USER=db_user
DB_PASSWORD=secure-password
```

### Perfiles Spring Boot
```bash
# Desarrollo (H2 en memoria)
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"

# MySQL
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mysql"

# Production
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

---

## 🔐 SEGURIDAD

### Autenticación JWT

**Flujo:**
1. Cliente envía credenciales → Patient Service
2. Se valida y se genera JWT token
3. Cliente usa token en header `Authorization: Bearer <token>`
4. Gateway valida token antes de rutear

**Ejemplo:**
```bash
# 1. Login (obtener token)
curl -X POST http://localhost:8080/api/patients/login \
  -H "Content-Type: application/json" \
  -d '{"username":"doctor1","password":"pass123"}'

# Respuesta:
# {
#   "token": "eyJhbGciOiJIUzUxMiJ9...",
#   "expiresIn": 86400000
# }

# 2. Usar token en peticiones
curl -X GET http://localhost:8080/api/patients/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### Niveles de Seguridad

**Público** (sin autenticación requerida):
- `/swagger-ui.html`
- `/v3/api-docs/**`
- `/h2-console/**` (solo desarrollo)
- `/actuator/**`

**Autenticado** (JWT requerido):
- `/api/patients/**`
- `/api/doctors/**`
- `/api/appointments/**`
- `/api/records/**`
- `/api/payments/**`
- (todos los endpoints sensibles)

### Auditoría

Todos los accesos a datos sensibles se registran automáticamente:

```bash
# Ver auditoría de un usuario
curl -X GET "http://localhost:8092/api/audit/user/doctor-123?page=0&size=10" \
  -H "Authorization: Bearer <token>"

# Ver auditoría de un recurso específico
curl -X GET http://localhost:8092/api/audit/resource/patient-456 \
  -H "Authorization: Bearer <token>"

# Rastrear una operación completa por Correlation ID
curl -X GET "http://localhost:8092/api/audit/correlation/abc123xyz" \
  -H "Authorization: Bearer <token>"
```

---

## 📦 DESPLIEGUE

### Docker Compose (Recomendado)

```bash
docker-compose -f docker-compose.yml up -d
```

Ver archivo `docker-compose.yml` para detalles.

### Kubernetes

```bash
kubectl apply -f k8s/
```

Ver directorio `k8s/` para manifiestos.

### Manual (Servidor Linux)

```bash
# 1. Instalar Java 21
sudo apt-get install openjdk-21-jdk

# 2. Construir todos los servicios
mvn clean package -DskipTests

# 3. Crear directorio de logs
mkdir -p /var/log/telemedicina

# 4. Ejecutar servicio (systemd)
sudo cp systemd/telemedicina-services.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl start telemedicina-services
sudo systemctl enable telemedicina-services
```

---

## 🛠️ TROUBLESHOOTING

### Problema: "Audit-service no disponible"

**Síntomas:** Logs muestran `[FALLBACK] Audit-service no disponible`

**Solución:**
1. Verificar que audit-service está iniciado (puerto 8092)
2. Ver archivo `audit-fallback.log` para eventos guardados
3. Cuando audit-service recupere servicio, reprocesar fallback.log

```bash
# Ver archivo de fallback
cat audit-fallback.log | tail -20
```

### Problema: "JWT token expirado"

**Síntomas:** Error 401 Unauthorized en requests

**Solución:**
1. Obtener nuevo token
2. Aumentar `JWT_EXPIRATION` en `.env.local` (si necesario)
3. Verificar sincronización de clock del servidor

### Problema: "CORS error"

**Síntomas:** Error en consola: "Access to XMLHttpRequest blocked by CORS"

**Solución:**
1. Verificar dominio frontend en `CORS_ALLOWED_ORIGINS`
2. Asegurar que Gateway está corriendo (puerto 8080)
3. Usar HTTPS en producción (insecuro en desarrollo es normal)

### Problema: "Base de datos locked"

**Síntomas:** H2 Console muestra "Database is locked"

**Solución:**
```bash
# Reiniciar servicios (H2 libera locks al apagar)
pkill -f "java -jar"
mvn clean spring-boot:run -f audit-service/pom.xml
```

---

## 📚 DOCUMENTACIÓN

### Archivos Principales
- `ARQUITECTURA_MEJORADA.md` - Arquitectura completa y decisiones
- `README.md` (este archivo) - Guía de inicio rápido
- `docker-compose.yml` - Stack completo listo para desarrollo
- `.env.production.example` - Template seguro para producción
- `.env.development.example` - Template para desarrollo

### Endpoints Documentados via Swagger
- [Gateway Swagger](http://localhost:8080/swagger-ui.html)
- [Patient Service Swagger](http://localhost:8081/swagger-ui.html)
- [Audit Service Swagger](http://localhost:8092/swagger-ui.html)

### Guías
- Implementar nuevo servicio: `docs/NUEVO_SERVICIO.md` (próximamente)
- Integrar con Keycloak: `docs/KEYCLOAK.md` (próximamente)
- Actualizar a Kubernetes: `docs/K8S_UPGRADE.md` (próximamente)

---

## 📊 Stack Tecnológico

- **Framework:** Spring Boot 3.5.0
- **Java:** JDK 21
- **API Gateway:** Spring Cloud Gateway
- **Autenticación:** JWT (JJWT 0.11.5)
- **Persistencia:** JPA/Hibernate
- **Bases de Datos:** MySQL, PostgreSQL, H2
- **Logging:** SLF4J + Logback
- **Documentación API:** Swagger / OpenAPI 3.0
- **Build:** Maven 3.9+
- **Container:** Docker (próximo)
- **Orquestación:** Kubernetes (próximo)

---

## 🔄 Control de Versiones

### Branch Strategy
- `main` - Producción
- `develop` - Desarrollo
- `feature/*` - Nuevas características
- `bugfix/*` - Correcciones

### Commits
```bash
git commit -m "feat(patient-service): agregar validación de RUT"
git commit -m "fix(gateway): corregir manejo de CORS"
git commit -m "docs: mejorar README"
git commit -m "test: agregar tests de JWT"
```

---

## 📞 Soporte

- **Issues:** Usar GitHub Issues
- **Documentación:** Wiki del repositorio
- **Chat:** Slack #telemedicina-dev

---

## 📄 Licencia

Este proyecto está bajo licencia MIT. Ver `LICENSE` para detalles.

---

## ✅ Checklist de Producción

Antes de desplegar a producción:

- [ ] Cambiar `JWT_SECRET` a valor seguro (32+ caracteres)
- [ ] Cambiar `CORS_ALLOWED_ORIGINS` a dominios reales
- [ ] Configurar base de datos MySQL/PostgreSQL
- [ ] Configurar backups automáticos
- [ ] Configurar monitoreo (Prometheus + Grafana)
- [ ] Configurar logging centralizado (ELK)
- [ ] Configurar alertas (PagerDuty, Slack)
- [ ] Realizar pruebas de seguridad
- [ ] Realizar pruebas de carga
- [ ] Documentar runbooks de operación
- [ ] Entrenar al equipo de ops

---

**Última actualización:** Mayo 15, 2026  
**Versión:** 1.0.0-PROFESSIONAL  
**Estado:** ✅ Listo para desarrollo, testing y despliegue controlado

