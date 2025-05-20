package com.github.ajharry69.account.utils;

import com.github.ajharry69.account.AccountController;
import com.github.ajharry69.account.models.AccountResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class AccountAssembler implements RepresentationModelAssembler<AccountResponse, EntityModel<AccountResponse>> {
    @Override
    public EntityModel<AccountResponse> toModel(AccountResponse entity) {
        return EntityModel.of(entity).add(
                linkTo(
                        methodOn(AccountController.class).getCards(
                                entity.id(),
                                null,
                                null,
                                null,
                                null,
                                null,
                                false,
                                null
                        )
                ).withRel("cards")
        );
    }
}