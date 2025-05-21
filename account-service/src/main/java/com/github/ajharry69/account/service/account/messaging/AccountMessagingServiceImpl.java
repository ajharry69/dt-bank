package com.github.ajharry69.account.service.account.messaging;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AccountMessagingServiceImpl implements AccountMessagingService {
    private final AmqpTemplate template;

    @Override
    public void sendAccountDeletedEvent(AccountDeletedEvent event) {
        log.info("Sending account deleted event: {}", event);
        template.convertAndSend(
                AccountMessagingConfig.EXCHANGE_NAME,
                AccountMessagingConfig.ROUTING_KEY_DELETE_ACCOUNT,
                event
        );
        log.info("Account deleted event sent: {}", event);
    }
}
