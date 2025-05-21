package com.github.ajharry69.account.service.account.messaging;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AccountDeletedEvent(UUID accountId) {
}
