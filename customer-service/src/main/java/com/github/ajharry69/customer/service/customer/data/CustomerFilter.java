package com.github.ajharry69.customer.service.customer.data;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CustomerFilter(
        String name,
        LocalDate startDateCreated,
        LocalDate endDateCreated
) {
}
