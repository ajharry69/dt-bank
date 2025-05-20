package com.github.ajharry69.account.service.messaging.account;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AccountDeletedEvent(UUID accountId) {
}
