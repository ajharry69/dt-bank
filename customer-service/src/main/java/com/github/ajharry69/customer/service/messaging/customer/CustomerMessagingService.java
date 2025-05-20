package com.github.ajharry69.customer.service.messaging.customer;

public interface CustomerMessagingService {
    void sendCustomerDeletedEvent(CustomerDeletedEvent event);
}
