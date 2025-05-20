package com.github.ajharry69.card.service.messaging.account;

import com.github.ajharry69.card.CardService;
import com.github.ajharry69.card.data.CardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
class AccountMessagingConsumer {
    private final CardRepository repository;
    private final CardService service;

    @RabbitHandler
    @RabbitListener(queues = AccountMessagingConfig.QUEUE_NAME_DELETE_ACCOUNT)
    public void consumeAccountDeletedEvent(AccountDeletedEvent event) {
        log.info("Consuming account deleted event: {}", event);
        repository.findByAccountId(event.accountId())
                .forEach(account -> service.deleteCard(account.getId()));
        log.info("Consumed account deleted event: {}", event);
    }
}
