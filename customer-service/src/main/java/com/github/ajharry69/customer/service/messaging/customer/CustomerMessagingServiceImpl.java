package com.github.ajharry69.customer.service.messaging.customer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class CustomerMessagingServiceImpl implements CustomerMessagingService {
    private final AmqpTemplate template;

    @Override
    public void sendCustomerDeletedEvent(CustomerDeletedEvent event) {
        log.info("Sending customer deleted event: {}", event);
        template.convertAndSend(
                CustomerMessagingConfig.EXCHANGE_NAME,
                CustomerMessagingConfig.ROUTING_KEY_DELETE_CUSTOMER,
                event
        );
        log.info("Customer deleted event sent: {}", event);
    }
}
