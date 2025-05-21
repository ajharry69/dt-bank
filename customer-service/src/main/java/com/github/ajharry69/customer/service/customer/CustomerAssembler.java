package com.github.ajharry69.customer.service.customer;

import com.github.ajharry69.customer.service.account.AccountController;
import com.github.ajharry69.customer.service.customer.models.dtos.CustomerResponse;
import com.github.ajharry69.customer.service.account.data.AccountFilter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class CustomerAssembler implements RepresentationModelAssembler<CustomerResponse, EntityModel<CustomerResponse>> {
    @Override
    public EntityModel<CustomerResponse> toModel(CustomerResponse entity) {
        return EntityModel.of(entity)
                .add(linkTo(methodOn(CustomerController.class).getCustomer(entity.id())).withSelfRel())
                .add(
                        linkTo(
                                methodOn(AccountController.class)
                                        .getAccounts(entity.id(), AccountFilter.builder().build(), null)
                        ).withRel("accounts")
                );
    }
}