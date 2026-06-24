package cl.duoc.fullstack.appointmentservice.service;

import cl.duoc.fullstack.appointmentservice.controller.AppointmentController;
import cl.duoc.fullstack.appointmentservice.dto.AppointmentResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AppointmentLinkAssembler {

    public EntityModel<AppointmentResponseDTO> toModel(AppointmentResponseDTO appointment) {
        EntityModel<AppointmentResponseDTO> model = EntityModel.of(appointment);

        model.add(linkTo(methodOn(AppointmentController.class)
                .getByPatient(appointment.getPatientId())).withSelfRel());

        model.add(linkTo(methodOn(AppointmentController.class)
                .updateStatus(appointment.getId(), null)).withRel("update-status"));

        return model;
    }
}
