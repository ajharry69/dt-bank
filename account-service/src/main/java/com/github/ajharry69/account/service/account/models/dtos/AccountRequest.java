package com.github.ajharry69.account.service.account.models.dtos;

import com.github.ajharry69.account.constraints.BicSwift;
import com.github.ajharry69.account.constraints.Iban;
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
