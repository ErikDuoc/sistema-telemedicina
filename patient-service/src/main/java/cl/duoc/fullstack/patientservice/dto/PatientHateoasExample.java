package cl.duoc.fullstack.patientservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Ejemplo de respuesta HATEOAS para documentación OpenAPI.
 * Muestra la estructura de enlaces que se incluyen en las respuestas API.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
    description = "Respuesta de Paciente con enlaces HATEOAS",
    example = """
    {
      "id": 1,
      "rut": "12345678-K",
      "nombre": "Juan",
      "apellido": "Pérez",
      "fechaNacimiento": "1990-01-15",
      "genero": "M",
      "email": "juan@example.com",
      "prevision": "Fonasa",
      "contactosEmergencia": [],
      "_links": {
        "self": {"href": "http://localhost:8080/api/patients/1"},
        "all": {"href": "http://localhost:8080/api/patients"},
        "update": {"href": "http://localhost:8080/api/patients/1"},
        "delete": {"href": "http://localhost:8080/api/patients/1"}
      }
    }
    """
)
public class PatientHateoasExample {
    private Long id;
    private String rut;
    private String nombre;
    private String apellido;
    private String fechaNacimiento;
    private String genero;
    private String email;
    private String prevision;
    private Object contactosEmergencia;
    
    @Schema(description = "Enlaces HATEOAS para navegación hypermedia")
    private Map<String, LinkObject> links;

    @Getter
    @Builder
    public static class LinkObject {
        @Schema(example = "http://localhost:8080/api/patients/1")
        private String href;
    }
}
