# Defensa Individual - Miguel Mesias

## Tareas realizadas

- Eliminar context-paths de 10 servicios
  - Commit: ce34bf2
- Refactor @FeignClient para usar nombres lógicos y agregar fallbacks
  - Commits: d1651aa, fc0a4c0
- Agregar timeouts Feign en servicios relevantes
  - Commit: 321add8
- Crear pruebas E2E y tests de integración (Appointment)
  - Commit: 2f26650

## Evidencias técnicas

- Comandos ejecutados:
  - grep -r "context-path" */src/main/resources | wc -l  # Resultado: 0
  - grep -r "@FeignClient.*url=" */src/main/java --include="*Client.java" | wc -l  # Resultado: 0
- Archivos modificados destacados:
  - appointment-service/src/main/resources/application.yml (feign config)
  - appointment-service/src/main/java/.../client/*Client.java (refactor)
  - appointment-service/src/test/.../AppointmentE2ETest.java (nuevo)

## Conceptos técnicos dominados

- OpenFeign: migración de url hardcodeado a discovery via Eureka
- Fallbacks: prevenir fallas en cascada
- Timeouts de Feign: connectTimeout/readTimeout

## Dificultades y soluciones

- Push interrumpido por autenticación en la máquina local; dejé los commits locales para revisión del equipo antes de pushear.
- Maven no está disponible en este entorno, por lo que no pude ejecutar `mvn test` aquí; se recomienda ejecutar `mvn test -pl appointment-service,...` localmente.

## Commits relevantes (últimos)

- 2f26650 test: agregar pruebas E2E y PatientClientIT para integration testing
- 321add8 chore: agregar eureka client a poms necesarios
- fc0a4c0 feat: completar refactor @FeignClient y agregar fallbacks para laboratorio y agenda
- d1651aa feat: refactorizar @FeignClient para usar Eureka discovery
- ce34bf2 feat: eliminar context-path de todos los servicios

---

_Puedes revisar estos cambios localmente y, cuando el equipo confirme, puedo pushear todo al remoto._
