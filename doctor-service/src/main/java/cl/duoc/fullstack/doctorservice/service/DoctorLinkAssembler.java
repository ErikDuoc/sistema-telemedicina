package cl.duoc.fullstack.doctorservice.service;

import cl.duoc.fullstack.doctorservice.controller.DoctorController;
import cl.duoc.fullstack.doctorservice.dto.DoctorResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DoctorLinkAssembler {

    public EntityModel<DoctorResponseDTO> toModel(DoctorResponseDTO doctor) {
        EntityModel<DoctorResponseDTO> model = EntityModel.of(doctor);

        model.add(linkTo(methodOn(DoctorController.class)
                .getDoctorById(doctor.getId())).withSelfRel());

        model.add(linkTo(methodOn(DoctorController.class)
                .getAllDoctors()).withRel("all"));

        return model;
    }
}
