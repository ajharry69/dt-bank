package com.github.ajharry69.account.service.account.messaging;


public interface AccountMessagingService {
    void sendAccountDeletedEvent(AccountDeletedEvent event);
}
