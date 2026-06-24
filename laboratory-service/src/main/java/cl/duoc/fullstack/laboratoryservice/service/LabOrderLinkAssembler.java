package cl.duoc.fullstack.laboratoryservice.service;

import cl.duoc.fullstack.laboratoryservice.controller.LaboratoryController;
import cl.duoc.fullstack.laboratoryservice.dto.LabOrderResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LabOrderLinkAssembler {

    public EntityModel<LabOrderResponseDTO> toModel(LabOrderResponseDTO labOrder) {
        EntityModel<LabOrderResponseDTO> model = EntityModel.of(labOrder);

        model.add(linkTo(methodOn(LaboratoryController.class)
                .getPatientOrders(labOrder.getPatientId())).withSelfRel());

        model.add(linkTo(methodOn(LaboratoryController.class)
                .getAll()).withRel("all"));

        // Links condicionales según estado de la orden
        if ("PENDING".equalsIgnoreCase(labOrder.getStatus())) {
            model.add(linkTo(methodOn(LaboratoryController.class)
                    .create(null)).withRel("create"));
        }

        return model;
    }
}
