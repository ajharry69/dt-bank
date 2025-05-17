package com.github.ajharry69.customer;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CustomerFilter(
        String name,
        LocalDate startDateCreated,
        LocalDate endDateCreated
) {
}
