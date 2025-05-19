package com.github.ajharry69.customer.models;

import com.github.ajharry69.customer.utils.constraints.BicSwift;
import com.github.ajharry69.customer.utils.constraints.Iban;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateAccountRequest(
        @NotNull
        @Iban
        String iban,
        @NotNull
        @BicSwift
        String bicSwift) {
}
