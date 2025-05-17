package com.github.ajharry69.account;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AccountFilter(
        String iban,
        String bicSwift,
        LocalDate startDateCreated,
        LocalDate endDateCreated
) {
}
