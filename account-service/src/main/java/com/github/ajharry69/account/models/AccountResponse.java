package com.github.ajharry69.account.models;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AccountResponse(
        UUID id,
        String iban,
        String bicSwift,
        UUID customerId) {
}
