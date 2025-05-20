package com.github.ajharry69.customer.service.messaging.customer;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerDeletedEvent(UUID customerId) {
}
