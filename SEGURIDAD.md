# 🔒 GUÍA DE SEGURIDAD - TELEMEDICINA

## Documentación de Prácticas Seguras para Producción

---

## ⚠️ ITEMS CRÍTICOS ANTES DE PRODUCCIÓN

### 1. JWT Secret Management

**NUNCA hacer:**
```bash
❌ spring.jwt.secret: "your-secret-key-change-in-production"
❌ Hardcodear secrets en código
❌ Usar secretos iguales en dev, staging y prod
❌ Compartir secretos por Slack, email, etc
```

**SIEMPRE hacer:**
```bash
✅ Generar secreto aleatorio con 32+ caracteres:
   openssl rand -base64 32

✅ Guardarlo en variable de entorno:
   export JWT_SECRET=$(openssl rand -base64 32)
   
✅ Usar gestor de secretos:
   - HashiCorp Vault (recomendado)
   - AWS Secrets Manager
   - Google Cloud Secret Manager
   - Azure Key Vault
```

**Generar secreto seguro:**
```bash
# Opción 1: OpenSSL
openssl rand -base64 32

# Opción 2: Python
python3 -c "import secrets; print(secrets.token_urlsafe(32))"

# Opción 3: Java
java -cp . SecretGenerator

# Ejemplo de salida:
# dXP7k9vZ2mL4qR1nW5sX8yA3bC6dE9fG2hI5jK8mN0oP3qS6t9uV2wX5yZ8aB1c
```

### 2. CORS Configuration

**Desarrollo (localhost permitido):**
```yaml
cors:
  allowed-origins: "http://localhost:3000,http://localhost:4200,http://localhost:8080"
```

**Producción (específico a dominios reales):**
```yaml
cors:
  allowed-origins: "https://app.tudominio.com,https://tudominio.com"
```

**NUNCA en producción:**
```yaml
❌ cors.allowed-origins: "*"  # Permite cualquier origen
❌ cors.allow-credentials: true + wildcard origins  # Crítico
```

### 3. Base de Datos

**Configuración segura:**
```bash
✅ Usar credenciales diferentes para cada ambiente
✅ Usar SSL/TLS para conexión (jdbc:mysql://host/db?useSSL=true)
✅ Cambiar contraseña de base de datos por defecto
✅ Limitar acceso por IP firewall
✅ No exponer puerto de BD a internet
✅ Usar encriptación en reposo (Transparent Data Encryption)
```

**URL segura:**
```properties
# ✅ Correcto (con SSL)
spring.datasource.url=jdbc:mysql://db-host:3306/telemedicina?useSSL=true&serverTimezone=UTC

# ❌ Inseguro
spring.datasource.url=jdbc:mysql://db-host:3306/telemedicina
```

### 4. HTTPs/TLS

**En desarrollo:** HTTP es aceptable (localhost)

**En producción:** HTTPS OBLIGATORIO

```bash
✅ Obtener certificado SSL válido (Let's Encrypt, Digicert, etc)
✅ Configurar en servidor (nginx, Apache, Tomcat)
✅ Usar HSTS Header:
   spring.security.require-https=true
   server.servlet.session.cookie.secure=true
   server.servlet.session.cookie.http-only=true
```

**Configuración Spring Boot:**
```yaml
server:
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
  servlet:
    session:
      cookie:
        secure: true
        http-only: true
        same-site: strict
```

---

## 🔑 GESTIÓN DE SECRETOS RECOMENDADA

### Opción 1: HashiCorp Vault (Mejor práctica)

```bash
# Instalar Vault
wget https://releases.hashicorp.com/vault/1.14.0/vault_1.14.0_linux_amd64.zip
unzip vault_1.14.0_linux_amd64.zip
sudo mv vault /usr/local/bin/

# Iniciar Vault
vault server -dev

# Guardar secreto
vault kv put secret/telemedicina \
  jwt_secret="tu-secret-seguro" \
  db_password="tu-db-pass"

# Configurar Spring Boot
spring:
  cloud:
    vault:
      host: localhost
      port: 8200
      scheme: http
      authentication: TOKEN
      token: myroot
```

### Opción 2: Variables de Entorno (Simple)

```bash
# 1. En servidor production crear archivo seguro
sudo nano /etc/telemedicina.env
# Contenido:
# JWT_SECRET=dXP7k9vZ2mL4qR1nW5sX8yA3bC6dE9fG2hI5jK8mN0oP3qS6t9uV2wX5yZ8aB1c
# DB_PASSWORD=super-secure-password

# 2. Asegurar permisos
sudo chmod 600 /etc/telemedicina.env
sudo chown root:root /etc/telemedicina.env

# 3. En systemd service
[Service]
EnvironmentFile=/etc/telemedicina.env
ExecStart=/usr/bin/java -jar /opt/telemedicina/app.jar
```

### Opción 3: Docker Secrets (Kubernetes)

```bash
# Crear secrets en Kubernetes
kubectl create secret generic telemedicina-secrets \
  --from-literal=jwt-secret=dXP7k9vZ2mL4qR1nW5sX8yA3bC6dE9fG2hI5jK8mN0oP3qS6t9uV2wX5yZ8aB1c \
  --from-literal=db-password=super-secure-password

# En deployment.yaml
env:
  - name: JWT_SECRET
    valueFrom:
      secretKeyRef:
        name: telemedicina-secrets
        key: jwt-secret
```

---

## 🛡️ CHECKLIST DE SEGURIDAD

### Antes de Desplegar

- [ ] JWT_SECRET cambió a valor aleatorio seguro (32+ caracteres)
- [ ] CORS configurado solo con dominios reales (NO wildcard)
- [ ] Base de datos usa SSL/TLS
- [ ] Contraseña de BD es fuerte y única
- [ ] HTTPS/TLS está configurado en servidor
- [ ] Headers de seguridad activados:
  - [ ] X-Content-Type-Options: nosniff
  - [ ] X-Frame-Options: DENY
  - [ ] X-XSS-Protection: 1; mode=block
  - [ ] Strict-Transport-Security (HSTS)
- [ ] Audit Service está activo y funcional
- [ ] Fallback logs configurado en ruta segura
- [ ] Logging no expone información sensible
- [ ] Dependencies sin vulnerabilidades conocidas (mvn dependency-check)
- [ ] SQL Injection mitigado (usar parameterized queries)
- [ ] CSRF protegido
- [ ] Rate limiting activado en Gateway
- [ ] WAF (Web Application Firewall) configurado si disponible

### Operacional

- [ ] Backups automáticos de BD configurados
- [ ] Logs centralizados (ELK, CloudWatch, etc)
- [ ] Monitoreo de seguridad activado
- [ ] Alertas de acceso fallido configuradas
- [ ] Equipo notificado de credenciales
- [ ] Plan de respuesta a incidentes documentado
- [ ] Penetration testing completado

---

## 📋 AUDITORÍA DE SEGURIDAD

### Verificar JWT Configuration

```bash
# Ver que variable se usa (no hardcode)
grep -r "JWT_SECRET" . --exclude-dir=target

# Resultado correcto:
# ${JWT_SECRET:default-for-dev} ✅

# Resultado incorrecto:
# "my-actual-secret-value" ❌
```

### Verificar CORS

```bash
# Verificar que no hay wildcard en producción
grep -r "allowed-origins.*\*" ./production

# Si encuentra algo, es riesgo
```

### Verificar Variables de Entorno

```bash
# En servidor production, verificar que variables están cargadas
echo $JWT_SECRET
echo $DB_PASSWORD

# No deberían estar vacías en producción
```

### Verificar Certificados SSL

```bash
# Ver validez de certificado
openssl s_client -connect tudominio.com:443 -showcerts

# Verificar que no está expirado y está válido para el dominio
```

---

## 🔐 PROTECCIONES IMPLEMENTADAS

### ✅ Ya Incluído

1. **JWT Authentication**
   - Validación automática en Gateway
   - Tokens con expiración
   - Role-based access control

2. **Auditoría Distribuida**
   - Registro de todos los accesos
   - Correlation ID para trazabilidad
   - Fallback resiliente

3. **CORS Configurado**
   - Whitelist de dominios
   - Credenciales seguras

4. **Secrets Management**
   - Variables de entorno para secretos
   - Archivo .gitignore para archivos sensibles

5. **Logging Estructurado**
   - SLF4J sin exposición de datos sensibles
   - Correlation ID in logs

### ⚠️ Por Implementar (Fase 3)

1. **Rate Limiting**
   - Spring Security Rate Limiter
   - Gateway rate limit filter

2. **Web Application Firewall (WAF)**
   - AWS WAF, Cloudflare, ModSecurity
   - Protección contra SQL injection, XSS, etc

3. **Encriptación de Datos**
   - AES-256 para datos sensibles
   - Transparent Data Encryption en BD

4. **MFA/2FA**
   - TOTP (Time-based One-Time Password)
   - SMS o email verification

5. **Vulnerability Scanning**
   - OWASP Dependency Check
   - SonarQube
   - Snyk

6. **Penetration Testing**
   - Testing de terceros
   - Bug bounty program

---

## 🚨 INCIDENCIA DE SEGURIDAD

### Si Se Compromiete una Credencial

```bash
# 1. INMEDIATAMENTE rotar el secreto
export JWT_SECRET=$(openssl rand -base64 32)

# 2. Notificar al equipo
# 3. Actualizar en vault/env vars
# 4. Revocar tokens existentes (logout all)
# 5. Revisar logs de auditoría
# 6. Hacer post-mortem

# Ver acceso sospechoso
curl "http://localhost:8092/api/audit/failed?page=0&size=100"
```

---

## 📚 REFERENCIAS

- OWASP Top 10: https://owasp.org/www-project-top-ten/
- JWT Best Practices: https://tools.ietf.org/html/rfc8725
- Spring Security: https://spring.io/projects/spring-security
- HashiCorp Vault: https://www.vaultproject.io/

---

**Mantener este documento actualizado.**  
**Última revisión:** Mayo 16, 2026

