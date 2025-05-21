package com.github.ajharry69.customer.service.customer.messaging;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerDeletedEvent(UUID customerId) {
}
