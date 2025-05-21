package com.github.ajharry69.card.service.card.models.dtos;

import com.github.ajharry69.card.constraints.Cvv;
import com.github.ajharry69.card.constraints.Pan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateCardRequest(
        @NotNull
        String alias,
        @Pan
        @NotBlank
        String pan,
        @Cvv
        String cvv) {
}
