package com.github.ajharry69.account.service.messaging.customer;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerDeletedEvent(UUID customerId) {
}
