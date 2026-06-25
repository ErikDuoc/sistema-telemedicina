package cl.duoc.fullstack.paymentservice.service;

import cl.duoc.fullstack.paymentservice.controller.PaymentController;
import cl.duoc.fullstack.paymentservice.dto.PaymentResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PaymentLinkAssembler {

    public EntityModel<PaymentResponseDTO> toModel(PaymentResponseDTO payment) {
        EntityModel<PaymentResponseDTO> model = EntityModel.of(payment);

        model.add(linkTo(methodOn(PaymentController.class)
                .getById(payment.getId())).withSelfRel());

        model.add(linkTo(methodOn(PaymentController.class)
                .getAll()).withRel("all"));

        return model;
    }
}
