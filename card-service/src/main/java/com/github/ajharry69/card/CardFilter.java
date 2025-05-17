package com.github.ajharry69.card;

import com.github.ajharry69.card.models.CardType;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CardFilter(
        String pan,
        String alias,
        CardType type,
        LocalDate startDateCreated,
        LocalDate endDateCreated
) {
}
