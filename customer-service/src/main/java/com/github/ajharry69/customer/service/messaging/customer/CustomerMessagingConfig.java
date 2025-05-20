package com.github.ajharry69.customer.service.messaging.customer;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerMessagingConfig {
    public static final String EXCHANGE_NAME = "CUSTOMER";
    public static final String QUEUE_NAME_DELETE_CUSTOMER = "QUEUE_DELETE_CUSTOMER";
    public static final String ROUTING_KEY_DELETE_CUSTOMER = QUEUE_NAME_DELETE_CUSTOMER + "." + EXCHANGE_NAME;

    @Bean
    public Queue deleteCustomerQueue() {
        return new Queue(QUEUE_NAME_DELETE_CUSTOMER, true);
    }

    @Bean
    public DirectExchange customerExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding deleteCustomerBinding(
            @Qualifier("deleteCustomerQueue")
            Queue queue,
            @Qualifier("customerExchange")
            DirectExchange exchange
    ) {
        return BindingBuilder.bind(queue).to(exchange)
                .with(ROUTING_KEY_DELETE_CUSTOMER);
    }
}