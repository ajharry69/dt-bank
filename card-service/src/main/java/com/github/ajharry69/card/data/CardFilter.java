package com.github.ajharry69.card.data;

import com.github.ajharry69.card.models.CardType;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CardFilter(
        boolean unmask,
        UUID accountId,
        String pan,
        String alias,
        CardType type,
        LocalDate startDateCreated,
        LocalDate endDateCreated
) {
}
