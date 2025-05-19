package com.github.ajharry69.account.models;

import com.github.ajharry69.account.utils.constraints.Cvv;
import com.github.ajharry69.account.utils.constraints.Pan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

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
        CardType type) {
}
