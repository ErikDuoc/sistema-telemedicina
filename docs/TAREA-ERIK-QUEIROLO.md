# 🚀 TAREA ERIK QUEIROLO — Infraestructura: Eureka + Gateway

**Rol**: Infraestructura y Coordinación Final  
**Responsabilidad**: Discovery Server, Gateway con lb://, Docker Compose, Validación Final, Matriz de Requerimientos

---

## OBJETIVO

- ✅ Crear `discovery-server` con Eureka habilitado
- ✅ Refactorizar `gateway-service` para usar rutas `lb://NOMBRE-SERVICIO`
- ✅ Actualizar `docker-compose.yml` con discovery-server y variables Eureka
- ✅ Coordinar validación final end-to-end
- ✅ Generar `docs/matriz-requerimientos.md` con trazabilidad completa

---

## 📋 PASO 1: CREAR DISCOVERY-SERVER

### 1.1 Crear estructura de carpetas

```bash
mkdir -p discovery-server/src/main/java/cl/duoc/fullstack/discoveryserver
mkdir -p discovery-server/src/main/resources
mkdir -p discovery-server/src/test/java
mkdir -p discovery-server/.mvn
```

### 1.2 Copiar scripts Maven

```bash
cp gateway-service/mvnw discovery-server/
cp gateway-service/mvnw.cmd discovery-server/
cp -r gateway-service/.mvn/* discovery-server/.mvn/
chmod +x discovery-server/mvnw
```

### 1.3 Crear `discovery-server/pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.0</version>
        <relativePath/>
    </parent>

    <groupId>cl.duoc.fullstack</groupId>
    <artifactId>discovery-server</artifactId>
    <version>1.0.0</version>
    <name>discovery-server</name>

    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2025.0.0</spring-cloud.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

### 1.4 Crear Application class

**Archivo**: `discovery-server/src/main/java/cl/duoc/fullstack/discoveryserver/DiscoveryServerApplication.java`

```java
package cl.duoc.fullstack.discoveryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }

}
```

### 1.5 Crear `discovery-server/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: discovery-server

server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
  server:
    enableSelfPreservation: false

management:
  endpoints:
    web:
      exposure:
        include: health,info
```

### 1.6 Crear `discovery-server/src/main/resources/application-mysql.yml`

```yaml
spring:
  application:
    name: discovery-server

server:
  port: 8761

eureka:
  instance:
    hostname: discovery-server
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://discovery-server:${server.port}/eureka/
  server:
    enableSelfPreservation: false
```

### 1.7 Crear `discovery-server/Dockerfile`

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline || true

COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-jammy
LABEL maintainer="equipo-de-desarrollo"

RUN groupadd -r spring && useradd -r -g spring spring

WORKDIR /app

COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8761

ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=mysql

USER spring

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar /app/app.jar"]
```

### 1.8 Actualizar `pom.xml` raíz

Editar raíz `pom.xml` y agregar `discovery-server` a la sección `<modules>`:

```xml
    <modules>
        <module>agenda-service</module>
        <module>appointment-service</module>
        <module>clinical-record-service</module>
        <module>doctor-service</module>
        <module>laboratory-service</module>
        <module>notification-service</module>
        <module>patient-service</module>
        <module>payment-service</module>
        <module>prescription-service</module>
        <module>video-consultation-service</module>
        <module>discovery-server</module>
        <module>gateway-service</module>
    </modules>
```

### 1.9 Compilar discovery-server

```bash
mvn clean install -pl discovery-server
```

**Resultado esperado**: `BUILD SUCCESS`

---

## 📋 PASO 2: REFACTORIZAR GATEWAY-SERVICE

### 2.1 Actualizar `gateway-service/pom.xml`

Buscar sección `<dependencies>` y agregar (si no existe):

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### 2.2 Reemplazar `gateway-service/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: gateway-service
  cloud:
    gateway:
      routes:
        - id: patient-service
          uri: lb://patient-service
          predicates:
            - Path=/api/patients/**
        - id: doctor-service
          uri: lb://doctor-service
          predicates:
            - Path=/api/doctors/**
        - id: notification-service
          uri: lb://notification-service
          predicates:
            - Path=/api/notifications/**
        - id: payment-service
          uri: lb://payment-service
          predicates:
            - Path=/api/payments/**
        - id: agenda-service
          uri: lb://agenda-service
          predicates:
            - Path=/api/agenda/**
        - id: laboratory-service
          uri: lb://laboratory-service
          predicates:
            - Path=/api/lab/**
        - id: appointment-service
          uri: lb://appointment-service
          predicates:
            - Path=/api/appointments/**
        - id: clinical-record-service
          uri: lb://clinical-record-service
          predicates:
            - Path=/api/clinical-records/**
        - id: prescription-service
          uri: lb://prescription-service
          predicates:
            - Path=/api/prescriptions/**
        - id: video-consultation-service
          uri: lb://video-consultation-service
          predicates:
            - Path=/api/video-consultations/**

server:
  port: 8080

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
  instance:
    preferIpAddress: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,gateway
```

### 2.3 Compilar gateway

```bash
mvn clean install -pl gateway-service
```

---

## 📋 PASO 3: ACTUALIZAR DOCKER-COMPOSE.YML

Reemplazar completamente `docker-compose.yml` con la versión que incluye `discovery-server` y variables Eureka (proporcionada separadamente — contacta a Genaro si necesitas).

**Cambios clave**:
- ✅ Agregar servicio `discovery-server` primero
- ✅ Agregar `healthcheck` en discovery-server
- ✅ Agregar `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` a todos los servicios
- ✅ Actualizar `depends_on` con `condition: service_healthy`

---

## 📋 PASO 4: EJECUTAR Y VALIDAR

### 4.1 Compilar todo

```bash
mvn clean install
```

**Resultado esperado**: `BUILD SUCCESS`

### 4.2 Levantar con Docker Compose

```bash
docker-compose up -d
sleep 60
docker-compose ps
```

**Resultado esperado**: Todos servicios deben tener estado `Up`

### 4.3 Verificar Eureka Dashboard

Abre en navegador: **http://localhost:8761**

**Resultado esperado**:
- Dashboard visible
- "Instances currently registered with Eureka" mostrando ~10 servicios
- Cada servicio con estado **UP** (color verde)

**Captura evidencia**: Toma screenshot y guárdalo en `docs/evidencia-eureka-dashboard.png`

### 4.4 Verificar Gateway

```bash
curl -X GET http://localhost:8080/api/patients
```

**Resultado esperado**: 200 OK con JSON (o error controlado, NO 404)

### 4.5 Ver logs

```bash
docker-compose logs discovery-server | grep -i "registered\|eureka"
docker-compose logs gateway | grep -i "routed\|gateway"
```

---

## 📋 PASO 5: GENERAR MATRIZ DE REQUERIMIENTOS

### 5.1 Crear `docs/matriz-requerimientos.md`

Este archivo debe mapear **TODOS** los requerimientos de la pauta con trazabilidad.

**Estructura mínima**:

| ID | Requerimiento | Tipo | Estado | Endpoint/Evidencia | Prueba |
|----|---|---|---|---|---|
| RF-01 | Crear paciente | Funcional | ✅ | `POST /api/patients` | `PatientControllerTest.crearPaciente_ok` |
| INF-01 | API Gateway | Infraestructura | ✅ | `http://localhost:8080`, rutas en `application.yml` | Llamada exitosa a `/api/patients` |
| INF-02 | Eureka Server | Infraestructura | ✅ | `http://localhost:8761`, 10+ servicios UP | Dashboard con instancias registradas |

**Tareas para completar matriz** (coordinadas con Miguel y Genaro):
- Todos RF (requerimientos funcionales) - responsable de cada servicio
- Todas RN (reglas negocio) - responsable de cada servicio
- Todas INF (infraestructura) - **tú coordinas**

---

## 📋 PASO 6: DOCUMENTO DE DEFENSA INDIVIDUAL

### 6.1 Crear `docs/defensa-individual/Erik-Queirolo.md`

Incluir:

```markdown
# Defensa Individual - Erik Queirolo

## Tareas Realizadas

### Discovery Server
- **Archivos creados**: pom.xml, DiscoveryServerApplication.java, application.yml, Dockerfile
- **Commits**: [HASH-1] crear discovery-server, [HASH-2] agregar Eureka config

### Gateway
- **Archivos modificados**: gateway-service/pom.xml, application.yml
- **Commits**: [HASH-3] agregar eureka-client, [HASH-4] cambiar rutas a lb://

### Docker Compose
- **Archivos modificados**: docker-compose.yml (raíz)
- **Commits**: [HASH-5] agregar discovery-server, [HASH-6] agregar Eureka vars

## Evidencias Técnicas

### Eureka Dashboard
- Captura en `docs/evidencia-eureka-dashboard.png`
- 10+ servicios registrados, estado UP

### Gateway Funcionando
- Resultado: `curl http://localhost:8080/api/patients` → 200 OK
- Logs mostrando rutas lb://

### Conceptos Técnicos
- ¿Qué problema resuelve Eureka? → [Explicación]
- ¿Diferencia entre host:puerto vs lb://nombre? → [Explicación]

## Matriz de Requerimientos
- Archivo: docs/matriz-requerimientos.md
- Trazabilidad completa de 50+ requerimientos
- Evidencias verificables
```

---

## ✅ CHECKLIST FINAL

- [ ] `discovery-server/` creado con estructura completa
- [ ] `discovery-server/pom.xml` compilando sin errores
- [ ] `gateway-service/pom.xml` actualizado con eureka-client
- [ ] `gateway-service/application.yml` con rutas `lb://`
- [ ] `pom.xml` raíz incluye módulo `discovery-server`
- [ ] `mvn clean install` exitoso (todo)
- [ ] `docker-compose up -d` levanta sin errores
- [ ] `http://localhost:8761` muestra Eureka Dashboard
- [ ] Eureka Dashboard muestra ~10 servicios con estado UP
- [ ] `curl http://localhost:8080/api/patients` retorna 200 OK
- [ ] Captura `docs/evidencia-eureka-dashboard.png` guardada
- [ ] `docs/matriz-requerimientos.md` creado y completo
- [ ] `docs/defensa-individual/Erik-Queirolo.md` creado
- [ ] Commits pusheados con mensajes significativos

---

## 🔄 SIGUIENTES PASOS

**Después de completar tu tarea:**

1. **Comunica a Miguel**: "Discovery + Gateway listos, pueden proceder con refactor Feign"
2. **Comunica a Genaro**: "Infraestructura lista, pueden proceder con migraciones"
3. **Espera confirmación**: Ambos completan sus tareas
4. **Ejecución final**: Coordina prueba end-to-end con todos servicios ejecutando
5. **Actualiza matriz**: Agrega evidencias finales (URLs Render, logs exitosos, etc.)

---

**¡Éxito! 🚀**

