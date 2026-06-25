package cl.duoc.fullstack.clinicalrecordservice.service;

import cl.duoc.fullstack.clinicalrecordservice.controller.ClinicalRecordController;
import cl.duoc.fullstack.clinicalrecordservice.dto.ClinicalRecordResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ClinicalRecordLinkAssembler {

    public EntityModel<ClinicalRecordResponseDTO> toModel(ClinicalRecordResponseDTO record) {
        EntityModel<ClinicalRecordResponseDTO> model = EntityModel.of(record);

        model.add(linkTo(methodOn(ClinicalRecordController.class)
                .getById(record.getId())).withSelfRel());

        return model;
    }
}
