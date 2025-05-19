package com.github.ajharry69.customer.utils;

import com.github.ajharry69.customer.CustomerController;
import com.github.ajharry69.customer.models.CustomerResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class CustomerAssembler implements RepresentationModelAssembler<CustomerResponse, EntityModel<CustomerResponse>> {
    @Override
    public EntityModel<CustomerResponse> toModel(CustomerResponse entity) {
        return EntityModel.of(entity).add(
                linkTo(
                        methodOn(CustomerController.class)
                                .getAccounts(entity.id(), null, null, null, null, null)
                ).withRel("accounts")
        );
    }
}