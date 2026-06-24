package cl.duoc.fullstack.prescriptionservice.service;

import cl.duoc.fullstack.prescriptionservice.controller.PrescriptionController;
import cl.duoc.fullstack.prescriptionservice.dto.PrescriptionResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PrescriptionLinkAssembler {

    public EntityModel<PrescriptionResponseDTO> toModel(PrescriptionResponseDTO prescription) {
        EntityModel<PrescriptionResponseDTO> model = EntityModel.of(prescription);

        model.add(linkTo(methodOn(PrescriptionController.class)
                .getById(prescription.getId())).withSelfRel());

        return model;
    }
}
