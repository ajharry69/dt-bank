package com.github.ajharry69.card.service.card.models.dtos;

import com.github.ajharry69.card.constraints.Cvv;
import com.github.ajharry69.card.constraints.Pan;
import com.github.ajharry69.card.service.card.models.CardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateCardRequest(
        @NotNull
        String alias,
        @Pan
        @NotBlank
        String pan,
        @Cvv
        String cvv,
        @NotNull
        CardType type,
        UUID accountId) {
}
