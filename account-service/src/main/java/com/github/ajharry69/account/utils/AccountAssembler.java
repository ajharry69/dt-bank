package com.github.ajharry69.account.utils;

import com.github.ajharry69.account.models.AccountResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

public class AccountAssembler implements RepresentationModelAssembler<AccountResponse, EntityModel<AccountResponse>> {
    @Override
    public EntityModel<AccountResponse> toModel(AccountResponse entity) {
        return EntityModel.of(
                entity
        );
    }
}