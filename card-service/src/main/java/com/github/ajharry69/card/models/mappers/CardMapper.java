package com.github.ajharry69.card.models.mappers;

import com.github.ajharry69.card.models.Card;
import com.github.ajharry69.card.models.CardCreateRequest;
import com.github.ajharry69.card.models.CardResponse;
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
    Card toEntity(CardCreateRequest request);

    @Mapping(target = "pan", expression = "java(\"*************\")")
    @Mapping(target = "cvv", expression = "java(\"***\")")
    CardResponse toResponse(Card card);

    CardResponse toUnmaskedResponse(Card card);
}
