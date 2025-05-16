package com.github.ajharry69.card.models;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CardResponse(
        UUID id,
        String alias,
        String pan,
        String cvv,
        CardType type,
        UUID accountId) {
}
