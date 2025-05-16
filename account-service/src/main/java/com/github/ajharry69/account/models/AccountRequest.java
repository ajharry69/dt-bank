package com.github.ajharry69.account.models;

import com.github.ajharry69.account.utils.constraints.BicSwift;
import com.github.ajharry69.account.utils.constraints.Iban;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AccountRequest(
        @NotNull
        @Iban
        String iban,
        @NotNull
        @BicSwift
        String bicSwift,
        UUID customerId) {
}
