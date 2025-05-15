package com.github.ajharry69.customer.utils;

import com.github.ajharry69.customer.models.CustomerResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

public class CustomerAssembler implements RepresentationModelAssembler<CustomerResponse, EntityModel<CustomerResponse>> {
    @Override
    public EntityModel<CustomerResponse> toModel(CustomerResponse entity) {
        return EntityModel.of(
                entity
        );
    }
}