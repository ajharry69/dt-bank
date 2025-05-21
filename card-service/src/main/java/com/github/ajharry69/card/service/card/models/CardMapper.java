package com.github.ajharry69.card.service.card.models;

import com.github.ajharry69.card.service.card.models.dtos.CardResponse;
import com.github.ajharry69.card.service.card.models.dtos.CreateCardRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CardMapper {
    @Mapping(target = "id", ignore = true)
    Card toEntity(CreateCardRequest request);

    @Mapping(target = "pan", expression = "java(\"*************\")")
    @Mapping(target = "cvv", expression = "java(\"***\")")
    CardResponse toResponse(Card card);

    CardResponse toUnmaskedResponse(Card card);
}
