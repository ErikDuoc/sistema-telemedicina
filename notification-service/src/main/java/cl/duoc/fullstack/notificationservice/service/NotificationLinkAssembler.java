package cl.duoc.fullstack.notificationservice.service;

import cl.duoc.fullstack.notificationservice.controller.NotificationController;
import cl.duoc.fullstack.notificationservice.dto.NotificationResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NotificationLinkAssembler {

    public EntityModel<NotificationResponseDTO> toModel(NotificationResponseDTO notification) {
        EntityModel<NotificationResponseDTO> model = EntityModel.of(notification);

        model.add(linkTo(methodOn(NotificationController.class)
                .getById(notification.getId())).withSelfRel());

        model.add(linkTo(methodOn(NotificationController.class)
                .getNotificationHistory(notification.getRecipientId())).withRel("history"));

        return model;
    }
}
