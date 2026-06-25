package cl.duoc.fullstack.laboratoryservice.service;

import cl.duoc.fullstack.laboratoryservice.controller.LaboratoryController;
import cl.duoc.fullstack.laboratoryservice.dto.LabOrderResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LaboratoryLinkAssembler {

    public EntityModel<LabOrderResponseDTO> toModel(LabOrderResponseDTO labOrder) {
        EntityModel<LabOrderResponseDTO> model = EntityModel.of(labOrder);

        model.add(linkTo(methodOn(LaboratoryController.class)
                .getPatientOrders(labOrder.getPatientId())).withSelfRel());

        model.add(linkTo(methodOn(LaboratoryController.class)
                .getPatientOrders(labOrder.getPatientId())).withRel("all"));

        if ("PENDIENTE".equalsIgnoreCase(labOrder.getStatus())) {
            model.add(linkTo(methodOn(LaboratoryController.class)
                    .createOrder(null)).withRel("create"));
        }

        return model;
    }
}
