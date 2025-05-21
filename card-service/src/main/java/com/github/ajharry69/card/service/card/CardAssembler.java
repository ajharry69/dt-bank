package com.github.ajharry69.card.service.card;

import com.github.ajharry69.card.service.card.models.dtos.CardResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class CardAssembler implements RepresentationModelAssembler<CardResponse, EntityModel<CardResponse>> {
    @Override
    public EntityModel<CardResponse> toModel(CardResponse entity) {
        return EntityModel.of(entity)
                .add(linkTo(methodOn(CardController.class).getCard(entity.id(), false)).withSelfRel());
    }
}