package com.github.ajharry69.customer.service.customer.messaging;

public interface CustomerMessagingService {
    void sendCustomerDeletedEvent(CustomerDeletedEvent event);
}
