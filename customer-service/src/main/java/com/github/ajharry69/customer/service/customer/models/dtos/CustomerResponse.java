package com.github.ajharry69.customer.service.customer.models.dtos;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerResponse(
        UUID id,
        String firstName,
        String lastName,
        String otherName) {
}
