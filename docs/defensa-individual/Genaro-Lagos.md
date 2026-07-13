# Defensa Individual – Genaro Lagos

## Información General

**Responsabilidad:** Migraciones Flyway, variables de entorno y documentación técnica.

**Rama:** `feature/genaro`

**Fecha:** 13 de julio de 2026

---

# 1. Resumen Ejecutivo

Durante esta tarea desarrollé las actividades asignadas correspondientes a la implementación de Flyway, configuración de variables de entorno y documentación del proyecto de telemedicina.

Las tareas principales realizadas fueron:

- Configuración de Flyway en los microservicios correspondientes.
- Creación de migraciones SQL para las entidades asignadas.
- Creación del archivo `.env.example`.
- Actualización del README.
- Elaboración de documentación técnica.
- Elaboración de documentación funcional.
- Actualización de la matriz de requerimientos.
- Elaboración de esta defensa individual.

---

# 2. Archivos creados

Se incorporaron los siguientes archivos:

- `.env.example`
- `docs/documentacion-tecnica.md`
- `docs/documentacion-funcional.md`
- `docs/defensa-individual/Genaro-Lagos.md`
- Archivos de migración SQL ubicados en:
   - agenda-service
   - appointment-service
   - clinical-record-service
   - doctor-service
   - laboratory-service
   - notification-service
   - patient-service
   - payment-service
   - prescription-service
   - video-consultation-service

---

# 3. Archivos modificados

Se realizaron modificaciones principalmente en:

- README.md
- docs/matriz-requerimientos.md
- clinical-record-service/pom.xml
- application.yml de los microservicios correspondientes
- application-mysql.yml de los microservicios correspondientes

Estas modificaciones permitieron integrar Flyway sin eliminar configuraciones existentes.

---

# 4. Trabajo realizado

## Paso 1 – Migraciones Flyway

Se crearon los directorios `db/migration` necesarios.

Se incorporaron migraciones SQL para los servicios asignados respetando el modelo de datos existente.

En el caso de `patient-service` se respetó la numeración existente de las migraciones, agregando nuevos scripts sin reemplazar los ya presentes.

---

## Paso 2 – Variables de entorno

Se creó un archivo `.env.example` con variables de ejemplo necesarias para el funcionamiento del proyecto.

No se incorporaron credenciales reales.

---

## Paso 3 – Documentación técnica

Se elaboró documentación relacionada con:

- Arquitectura.
- Microservicios.
- Base de datos.
- Flyway.
- Variables de entorno.
- Configuración general.

---

## Paso 4 – Documentación funcional

Se documentó el funcionamiento general del sistema de telemedicina y de sus principales módulos.

---

## Paso 5 – Matriz de requerimientos

Se actualizó la matriz de requerimientos incorporando el estado actual de los elementos desarrollados durante esta tarea.

Los elementos que requieren validación mediante Docker o Maven fueron marcados como pendientes de validación.

---

## Paso 6 – README

Se actualizó el README agregando información relacionada con:

- Flyway.
- Variables de entorno.
- Configuración general.
- Ejecución del proyecto.

---

## Paso 7 – Defensa individual

Se elaboró el presente documento como evidencia del trabajo desarrollado.

---

# 5. Decisiones técnicas

Durante la implementación se adoptaron las siguientes decisiones:

- Utilizar Flyway para el control de versiones de la base de datos.
- Configurar Hibernate utilizando `ddl-auto=validate` para evitar modificaciones automáticas del esquema.
- Mantener la configuración YAML existente incorporando únicamente las propiedades necesarias.
- Evitar claves foráneas entre microservicios debido a que cada servicio posee su propia base de datos.
- Mantener compatibilidad con la estructura existente del proyecto.

---

# 6. Evidencias técnicas

Se generaron:

- Migraciones SQL.
- Configuración Flyway.
- Archivo `.env.example`.
- Documentación técnica.
- Documentación funcional.
- Actualización de README.
- Actualización de matriz de requerimientos.

Las modificaciones pueden verificarse mediante el historial de Git y los archivos incorporados al repositorio.

---

# 7. Comandos de validación

Las siguientes validaciones fueron consideradas para el proyecto:

```bash
git status

git diff --check

mvn clean install

docker compose config

docker compose up -d
```

Las validaciones que requieren Maven y Docker quedaron pendientes de ejecución en un entorno completamente configurado.

---

# 8. Conceptos técnicos aplicados

Durante el desarrollo se utilizaron los siguientes conceptos:

- Spring Boot
- Spring Data JPA
- Flyway
- Hibernate
- MySQL
- H2
- Docker
- Docker Compose
- Variables de entorno
- Arquitectura de Microservicios
- Git
- GitHub

---

# 9. Dificultades encontradas

Durante el desarrollo se presentaron principalmente:

- Compatibilidad entre la numeración de migraciones existentes.
- Integración de Flyway respetando configuraciones YAML existentes.
- Evitar modificaciones fuera del alcance de la tarea.
- Mantener compatibilidad con los distintos perfiles de configuración.

Cada situación fue corregida respetando la estructura original del proyecto.

---

# 10. Validaciones pendientes

Quedaron pendientes de ejecutar en un entorno completamente configurado:

- Compilación mediante Maven.
- Ejecución de Docker Compose.
- Ejecución real de las migraciones Flyway.
- Ejecución de pruebas automatizadas.

No se declaran resultados de pruebas que no hayan sido ejecutadas.

---

# 11. Commits

Los commits serán realizados posteriormente.

**Hashes de commits:**

PENDIENTE: completar una vez realizados los commits.

---

# 12. Conclusión

Se desarrollaron las actividades asignadas correspondientes a Flyway, migraciones SQL, variables de entorno y documentación del proyecto.

Las modificaciones quedaron preparadas en la rama `feature/genaro` para su revisión antes de realizar los commits y posterior integración al repositorio principal.