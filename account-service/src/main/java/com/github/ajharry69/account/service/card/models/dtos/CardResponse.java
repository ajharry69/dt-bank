package com.github.ajharry69.account.service.card.models.dtos;

import com.github.ajharry69.account.service.card.models.CardType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CardResponse(
        UUID id,
        String alias,
        String pan,
        String cvv,
        CardType type) {
}
