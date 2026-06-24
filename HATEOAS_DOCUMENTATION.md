/**
 * DOCUMENTACIÓN DE IMPLEMENTACIÓN HATEOAS
 * 
 * Este documento describe cómo se implementó HATEOAS en la API de telemedicina.
 * 
 * HATEOAS (Hypermedia As The Engine Of Application State) es un principio REST
 * que permite a los clientes navegar la API únicamente a través de enlaces dinámicos
 * devueltos por el servidor, sin necesidad de conocer las URLs de antemano.
 * 
 * ============================================================================
 * 1. ESTRUCTURA DE RESPUESTAS CON HATEOAS
 * ============================================================================
 * 
 * Respuesta de un recurso individual:
 * 
 * {
 *   "id": 1,
 *   "nombre": "Juan Pérez",
 *   "email": "juan@example.com",
 *   "_links": {
 *     "self": {
 *       "href": "http://localhost:8080/api/patients/1"
 *     },
 *     "all": {
 *       "href": "http://localhost:8080/api/patients"
 *     },
 *     "update": {
 *       "href": "http://localhost:8080/api/patients/1"
 *     },
 *     "delete": {
 *       "href": "http://localhost:8080/api/patients/1"
 *     }
 *   }
 * }
 * 
 * Respuesta de una colección:
 * 
 * {
 *   "_embedded": {
 *     "patientResponseDTOList": [
 *       {
 *         "id": 1,
 *         "nombre": "Juan Pérez",
 *         "_links": {
 *           "self": { "href": "http://localhost:8080/api/patients/1" }
 *         }
 *       }
 *     ]
 *   },
 *   "_links": {
 *     "self": { "href": "http://localhost:8080/api/patients" }
 *   }
 * }
 * 
 * ============================================================================
 * 2. ENLACES DISPONIBLES
 * ============================================================================
 * 
 * - self: Link a la representación actual del recurso
 * - all: Link a la colección completa de recursos
 * - update: Link para actualizar el recurso (condicional según estado)
 * - delete: Link para eliminar el recurso
 * - cancel: Link para cancelar (ej: cita médica en estado CONFIRMED)
 * 
 * ============================================================================
 * 3. LINKS CONDICIONALES
 * ============================================================================
 * 
 * Los enlaces pueden variar según el estado del recurso:
 * 
 * PACIENTE:
 * - update: Solo si prevision != null (paciente activo)
 * 
 * DOCTOR:
 * - create: Solo si specialtyName != null (doctor activo)
 * 
 * CITA:
 * - update-status: Si status = "OPEN"
 * - cancel: Si status = "CONFIRMED"
 * 
 * ORDEN LABORATORIO:
 * - create: Si status = "PENDING"
 * 
 * ============================================================================
 * 4. ENDPOINTS IMPLEMENTADOS CON HATEOAS
 * ============================================================================
 * 
 * GET /api/patients
 * Retorna: CollectionModel<EntityModel<PatientResponseDTO>>
 * 
 * GET /api/patients/{id}
 * Retorna: EntityModel<PatientResponseDTO>
 * 
 * GET /api/doctors
 * Retorna: CollectionModel<EntityModel<DoctorResponseDTO>>
 * 
 * GET /api/doctors/{id}/profile
 * Retorna: EntityModel<DoctorResponseDTO>
 * 
 * [Similar para otros endpoints...]
 * 
 * ============================================================================
 * 5. VENTAJAS DE HATEOAS
 * ============================================================================
 * 
 * 1. Discovery: El cliente descubre endpoints disponibles dinámicamente
 * 2. Bajo acoplamiento: Los clientes no necesitan saber las URLs
 * 3. Evolución de API: Se pueden cambiar rutas sin romper clientes
 * 4. Navegación guiada: Los enlaces indican las acciones posibles
 * 5. RESTful completo: Cumple con el nivel 3 de Richardson Maturity Model
 * 
 * ============================================================================
 * 6. CÓMO USAR EN EL CLIENTE
 * ============================================================================
 * 
 * // 1. Obtener un paciente
 * GET /api/patients/1
 * 
 * // 2. Usar el link "update" de la respuesta
 * PUT /api/patients/1
 * {
 *   "nombre": "Juan Carlos",
 *   ...
 * }
 * 
 * // 3. Usar el link "all" para volver a la lista
 * GET /api/patients
 * 
 * // 4. Los links condicionales indican qué acciones son permitidas
 * // Si no hay "delete" link, el cliente sabe que no puede eliminar
 * 
 */
public class HateoasDocumentation {
}
