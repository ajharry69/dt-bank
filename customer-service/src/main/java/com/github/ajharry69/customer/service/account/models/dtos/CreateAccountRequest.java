package com.github.ajharry69.customer.service.account.models.dtos;

import lombok.Builder;

@Builder
public record CreateAccountRequest(
        String iban,
        String bicSwift) {
}
