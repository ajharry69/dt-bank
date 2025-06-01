package com.github.ajharry69.account.service.customer.messaging;

import com.github.ajharry69.account.service.account.AccountService;
import com.github.ajharry69.account.service.account.data.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
class CustomerMessagingConsumer {
    private final AccountRepository repository;
    private final AccountService service;

    @RabbitHandler
    @RabbitListener(queues = CustomerMessagingConfig.QUEUE_NAME_DELETE_CUSTOMER)
    public void consumeCustomerDeletedEvent(CustomerDeletedEvent event) {
        log.info("Consuming customer deleted event: {}", event);
        repository.findByCustomerId(event.customerId())
                .forEach(account -> service.deleteAccount(account.getId()));
        log.info("Consumed customer deleted event: {}", event);
    }
}
