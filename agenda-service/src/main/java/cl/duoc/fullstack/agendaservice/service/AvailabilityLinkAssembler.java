package cl.duoc.fullstack.agendaservice.service;

import cl.duoc.fullstack.agendaservice.controller.AvailabilityController;
import cl.duoc.fullstack.agendaservice.dto.AvailabilityResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AvailabilityLinkAssembler {

    public EntityModel<AvailabilityResponseDTO> toModel(AvailabilityResponseDTO availability) {
        EntityModel<AvailabilityResponseDTO> model = EntityModel.of(availability);

        model.add(linkTo(methodOn(AvailabilityController.class)
                .getById(availability.getId())).withSelfRel());

        model.add(linkTo(methodOn(AvailabilityController.class)
                .getAll()).withRel("all"));

        return model;
    }
}
