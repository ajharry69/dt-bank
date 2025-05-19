package com.github.ajharry69.account.models.mappers;

import com.github.ajharry69.account.service.card.dtos.CreateCardRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CardMapper {
    CreateCardRequest toCreateCardRequest(com.github.ajharry69.account.models.CreateCardRequest request);
}
