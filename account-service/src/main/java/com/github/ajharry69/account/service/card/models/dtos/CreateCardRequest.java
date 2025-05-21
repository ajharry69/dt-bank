package com.github.ajharry69.account.service.card.models.dtos;

import com.github.ajharry69.account.service.card.models.CardType;
import lombok.Builder;

@Builder
public record CreateCardRequest(
        String alias,
        String pan,
        String cvv,
        CardType type) {
}
