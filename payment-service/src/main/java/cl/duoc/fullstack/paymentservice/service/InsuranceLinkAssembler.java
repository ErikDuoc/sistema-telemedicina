package cl.duoc.fullstack.paymentservice.service;

import cl.duoc.fullstack.paymentservice.controller.PaymentController;
import cl.duoc.fullstack.paymentservice.dto.InsuranceDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class InsuranceLinkAssembler {

    public EntityModel<InsuranceDTO> toModel(InsuranceDTO insurance) {
        EntityModel<InsuranceDTO> model = EntityModel.of(insurance);

        model.add(linkTo(methodOn(PaymentController.class)
                .getAllInsurances()).withSelfRel());

        return model;
    }
}
