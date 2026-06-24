package cl.duoc.fullstack.videoconsultationservice.service;

import cl.duoc.fullstack.videoconsultationservice.controller.VideoConsultationController;
import cl.duoc.fullstack.videoconsultationservice.dto.VideoConsultationResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class VideoConsultationLinkAssembler {

    public EntityModel<VideoConsultationResponseDTO> toModel(VideoConsultationResponseDTO consultation) {
        EntityModel<VideoConsultationResponseDTO> model = EntityModel.of(consultation);

        model.add(linkTo(methodOn(VideoConsultationController.class)
                .getById(consultation.getId())).withSelfRel());

        return model;
    }
}
