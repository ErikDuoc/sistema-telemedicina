package cl.duoc.fullstack.laboratoryservice.service;

import cl.duoc.fullstack.laboratoryservice.controller.LabOrderController;
import cl.duoc.fullstack.laboratoryservice.dto.LabOrderResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LabOrderLinkAssembler {

    public EntityModel<LabOrderResponseDTO> toModel(LabOrderResponseDTO labOrder) {
        EntityModel<LabOrderResponseDTO> model = EntityModel.of(labOrder);

        model.add(linkTo(methodOn(LabOrderController.class)
                .getById(labOrder.getId())).withSelfRel());

        model.add(linkTo(methodOn(LabOrderController.class)
                .getAll()).withRel("all"));

        return model;
    }
}
