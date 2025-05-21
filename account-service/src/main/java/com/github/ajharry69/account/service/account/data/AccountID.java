package com.github.ajharry69.account.service.account.data;

import com.github.ajharry69.account.service.account.models.Account;

import java.util.UUID;

/**
 * Projection for {@link Account}
 */
public interface AccountID {
    UUID getId();
}