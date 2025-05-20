package com.github.ajharry69.customer.models;

import lombok.Builder;

@Builder
public record CreateAccountRequest(
        String iban,
        String bicSwift) {
}
