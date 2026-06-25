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

        // Links siempre presentes
        model.add(linkTo(methodOn(DoctorController.class)
                .getDoctorById(doctor.getId())).withSelfRel());

        model.add(linkTo(methodOn(DoctorController.class)
                .getAllDoctors()).withRel("all"));

        // Links condicionales: si el doctor está activo
        if (isDoctorActive(doctor)) {
            model.add(linkTo(methodOn(DoctorController.class)
                    .createDoctor(null)).withRel("create"));
        }

        return model;
    }

    /**
     * Valida si el doctor está activo.
     */
    private boolean isDoctorActive(DoctorResponseDTO doctor) {
        // Ejemplo: verificar que tiene especialidad registrada
        return doctor.getSpecialtyName() != null && !doctor.getSpecialtyName().isEmpty();
    }
}
