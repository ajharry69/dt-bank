package com.github.ajharry69.card.models;

import com.github.ajharry69.card.utils.constraints.Cvv;
import com.github.ajharry69.card.utils.constraints.Pan;
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
