package com.github.ajharry69.account.service.account;

import com.github.ajharry69.account.service.account.models.dtos.AccountResponse;
import com.github.ajharry69.account.service.card.CardController;
import com.github.ajharry69.account.service.card.data.CardFilter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class AccountAssembler implements RepresentationModelAssembler<AccountResponse, EntityModel<AccountResponse>> {
    @Override
    public EntityModel<AccountResponse> toModel(AccountResponse entity) {
        return EntityModel.of(entity)
                .add(linkTo(methodOn(AccountController.class).getAccount(entity.id())).withSelfRel())
                .add(linkTo(methodOn(CardController.class).getCards(entity.id(), CardFilter.builder().build(), null)).withRel("cards"));
    }
}