package com.github.ajharry69.customer.service.account.dtos;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AccountResponse(
        UUID id,
        String iban,
        String bicSwift) {
}
