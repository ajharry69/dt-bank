package com.github.ajharry69.customer.service.account;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record AccountFilter(
        UUID customerId,
        String iban,
        String bicSwift,
        LocalDate startDateCreated,
        LocalDate endDateCreated
) {
}