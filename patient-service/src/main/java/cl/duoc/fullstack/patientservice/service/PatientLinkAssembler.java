package cl.duoc.fullstack.patientservice.service;

import cl.duoc.fullstack.patientservice.controller.PatientController;
import cl.duoc.fullstack.patientservice.dto.PatientResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PatientLinkAssembler {

    public EntityModel<PatientResponseDTO> toModel(PatientResponseDTO patient) {
        EntityModel<PatientResponseDTO> model = EntityModel.of(patient);

        // Links siempre presentes
        model.add(linkTo(methodOn(PatientController.class)
                .findById(patient.id())).withSelfRel());

        model.add(linkTo(methodOn(PatientController.class)
                .findAll()).withRel("all"));

        // Links condicionales según estado del paciente
        // Si el paciente está activo, permite actualización
        if (isPatientActive(patient)) {
            model.add(linkTo(methodOn(PatientController.class)
                    .update(patient.id(), null)).withRel("update"));
        }

        // Links condicionales según rol/permisos (aquí simplificado)
        // En producción, verificaría el contexto de seguridad
        model.add(linkTo(methodOn(PatientController.class)
                .delete(patient.id())).withRel("delete"));

        return model;
    }

    /**
     * Valida si el paciente está en estado activo.
     * Aquí se pueden agregar reglas de negocio.
     */
    private boolean isPatientActive(PatientResponseDTO patient) {
        // Ejemplo: verificar si el paciente tiene prevision
        return patient.prevision() != null && !patient.prevision().isEmpty();
    }
}

