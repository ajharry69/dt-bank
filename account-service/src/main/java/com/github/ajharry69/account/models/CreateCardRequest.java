package com.github.ajharry69.account.models;

import lombok.Builder;

@Builder
public record CreateCardRequest(
        String alias,
        String pan,
        String cvv,
        CardType type) {
}
