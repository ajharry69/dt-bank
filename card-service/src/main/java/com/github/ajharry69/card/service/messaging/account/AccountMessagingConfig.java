package com.github.ajharry69.card.service.messaging.account;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountMessagingConfig {
    public static final String EXCHANGE_NAME = "ACCOUNT";
    public static final String QUEUE_NAME_DELETE_ACCOUNT = "QUEUE_DELETE_ACCOUNT";
    public static final String ROUTING_KEY_DELETE_ACCOUNT = QUEUE_NAME_DELETE_ACCOUNT + "." + EXCHANGE_NAME;

    @Bean
    public Queue deleteAccountQueue() {
        return new Queue(QUEUE_NAME_DELETE_ACCOUNT, true);
    }

    @Bean
    public DirectExchange accountExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding deleteAccountBinding(
            @Qualifier("deleteAccountQueue")
            Queue queue,
            @Qualifier("accountExchange")
            DirectExchange exchange
    ) {
        return BindingBuilder.bind(queue).to(exchange)
                .with(ROUTING_KEY_DELETE_ACCOUNT);
    }
}
