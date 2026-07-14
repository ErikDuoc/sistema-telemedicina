# 9.1 Presentacion de defensa tecnica grupal

## Equipo

- Erik Queirolo
- Genaro Lagos
- Miguel Mesias

---

## 1) Problema y contexto del proyecto

El proyecto nace para resolver una necesidad academica y funcional: contar con una plataforma de telemedicina basada en microservicios que permita atencion remota, gestion clinica y operaciones de soporte (agenda, pagos, notificaciones, laboratorio, recetas y videoconsulta).

Problemas iniciales detectados en esta etapa:

- acoplamiento por rutas hardcodeadas entre servicios;
- arranque inestable en entorno Docker por dependencias sin salud validada;
- diferencias de configuracion entre local y contenedores;
- falta de estandarizacion en integracion, seguridad y documentacion tecnica.

---

## 2) Alcance final

Al cierre de esta etapa se deja una solucion integrada con:

- arquitectura de microservicios con API Gateway y Service Discovery (Eureka);
- despliegue reproducible con Docker Compose;
- servicios de dominio principales operativos (pacientes, doctores, citas, agenda, laboratorio, ficha clinica, recetas, pagos, notificaciones, videoconsultas);
- integracion entre servicios por descubrimiento dinamico;
- seguridad base con JWT/CORS segun configuraciones de cada servicio;
- documentacion tecnica y funcional para ejecucion, validacion y defensa.

---

## 3) Requerimientos principales

Requerimientos cubiertos en esta etapa:

- gestionar pacientes y doctores;
- registrar especialidades y disponibilidad medica;
- crear y consultar citas;
- mantener registros clinicos;
- gestionar ordenes de laboratorio y recetas;
- habilitar notificaciones y pagos;
- exponer APIs via Gateway como punto unico de entrada;
- permitir descubrimiento dinamico de servicios con Eureka;
- soportar despliegue local con Docker Compose;
- documentar APIs con Swagger/OpenAPI.

---

## 4) Correcciones realizadas a partir del feedback

Principales ajustes aplicados por el equipo:

- reemplazo de rutas hardcodeadas por `lb://service-name` en Gateway;
- ajuste de `@FeignClient` para evitar URLs fijas y mejorar desacoplamiento;
- incorporacion de `healthcheck` y `depends_on: condition: service_healthy` en Docker Compose;
- mejora de consistencia de datos legacy en `doctor-service` con backfill idempotente en `DataInitializer`;
- fortalecimiento de documentacion de arquitectura, despliegue y validacion.

---

## 5) Arquitectura

Arquitectura orientada a microservicios:

- `gateway-service` como entrada unica (`:8080`), enrutando por path;
- `discovery-server` (`:8761`) para registro y descubrimiento;
- servicios de negocio desacoplados, cada uno con su propio contexto de datos;
- comunicacion interna por nombre logico de servicio y no por IP fija;
- ejecucion orquestada por Docker Compose sobre red compartida.

---

## 6) Microservicios

Servicios principales de la solucion:

- `patient-service`
- `doctor-service`
- `appointment-service`
- `agenda-service`
- `clinical-record-service`
- `laboratory-service`
- `prescription-service`
- `payment-service`
- `notification-service`
- `video-consultation-service`
- `gateway-service`
- `discovery-server`

Cada servicio mantiene responsabilidad delimitada y contrato API independiente.

---

## 7) Modelo de datos

Criterios aplicados al modelo de datos:

- base relacional por dominio de negocio;
- separacion por esquema/base para evitar acoplamiento de tablas entre servicios;
- migraciones versionadas (Flyway) para estructura y datos seed;
- entidades y relaciones alineadas con el flujo clinico y administrativo.

Ejemplos de entidades de negocio:

- pacientes, doctores, especialidades;
- citas y disponibilidad;
- registros clinicos, recetas, examenes;
- pagos y notificaciones.

---

## 8) Flujo funcional principal

Flujo principal descrito para demostracion grupal: **Agendar cita**

1. Cliente invoca endpoint via Gateway.
2. Gateway enruta al `appointment-service`.
3. `appointment-service` valida paciente, doctor y disponibilidad (integraciones entre servicios).
4. Si cumple reglas, persiste la cita y devuelve respuesta de exito.
5. Se gatillan procesos complementarios (por ejemplo, notificacion) segun reglas del servicio.

Este flujo muestra integracion real entre API Gateway + Eureka + microservicios de dominio.

---

## 9) Seguridad

Controles de seguridad aplicados en esta etapa:

- autenticacion JWT en servicios configurados;
- control CORS para consumo desde frontends autorizados;
- filtros de seguridad por servicio y proteccion de endpoints;
- uso de variables de entorno para configuracion sensible;
- lineamientos de endurecimiento y buenas practicas documentados.

---

## 10) Pruebas

Estrategia de validacion utilizada:

- pruebas unitarias en servicios clave;
- pruebas de integracion para flujos entre microservicios;
- pruebas funcionales manuales via Postman/curl;
- validacion de arranque y salud en Docker Compose;
- verificacion de respuestas API y contratos JSON esperados.

---

## 11) Documentacion Swagger/OpenAPI

Cada microservicio expone documentacion de API para:

- explorar endpoints disponibles;
- revisar request/response y codigos HTTP;
- ejecutar pruebas rapidas con "Try it out";
- facilitar integracion y defensa tecnica con evidencia viva.

---

## 12) Despliegue local y remoto

### Local

- ejecucion de servicios con Docker Compose;
- levantamiento con orden controlado por healthchecks;
- acceso a Gateway (`:8080`) y Eureka (`:8761`) para validacion integral.

### Remoto

- se dejan lineamientos y artefactos para despliegue en plataformas cloud;
- parametrizacion por variables de entorno;
- enfoque en reproducibilidad, trazabilidad y separacion por ambientes.

---

## 13) Distribucion de responsabilidades (trabajo de esta etapa)

### Erik Queirolo

- implementacion/ajuste de infraestructura transversal;
- `discovery-server` y registro dinamico de servicios;
- refactor del `gateway-service` para enrutamiento `lb://`;
- orquestacion Docker Compose con orden de arranque y salud;
- hardening en `doctor-service` para reparar datos legacy incompletos.

### Genaro Lagos

- estandarizacion de migraciones Flyway en servicios;
- apoyo en configuracion de entornos y variables;
- documentacion tecnica/funcional y consolidacion de evidencias;
- soporte en definicion de despliegue local/remoto.

### Miguel Mesias

- refactor de integraciones entre servicios (Feign/discovery);
- ajustes para reducir acoplamiento entre APIs;
- fortalecimiento de validaciones y pruebas de integracion;
- apoyo en estabilizacion del flujo funcional principal.

---

## Cierre grupal

Como equipo, en esta etapa pasamos de una base funcional a una arquitectura mas profesional, reproducible y defendible tecnicamente: con discovery, gateway, integracion entre microservicios, mejoras de datos legacy, pruebas y documentacion para operacion y presentacion academica.
