package com.github.ajharry69.card.utils;

import com.github.ajharry69.card.models.CardResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

public class CardAssembler implements RepresentationModelAssembler<CardResponse, EntityModel<CardResponse>> {
    @Override
    public EntityModel<CardResponse> toModel(CardResponse entity) {
        return EntityModel.of(
                entity
        );
    }
}