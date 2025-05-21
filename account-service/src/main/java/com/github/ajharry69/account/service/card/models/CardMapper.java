package com.github.ajharry69.account.service.card.models;

import com.github.ajharry69.account.service.card.models.dtos.CreateAccountCardRequest;
import com.github.ajharry69.account.service.card.models.dtos.CreateCardRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CardMapper {
    CreateAccountCardRequest toCreateCardRequest(CreateCardRequest request);
}
