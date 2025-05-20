package com.github.ajharry69.account.service.messaging.account;


public interface AccountMessagingService {
    void sendAccountDeletedEvent(AccountDeletedEvent event);
}
