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

        model.add(linkTo(methodOn(PatientController.class)
                .findById(patient.id())).withSelfRel());

        model.add(linkTo(methodOn(PatientController.class)
                .findAll()).withRel("all"));

        model.add(linkTo(methodOn(PatientController.class)
                .update(patient.id(), null)).withRel("update"));

        model.add(linkTo(methodOn(PatientController.class)
                .delete(patient.id())).withRel("delete"));

        return model;
    }
}
