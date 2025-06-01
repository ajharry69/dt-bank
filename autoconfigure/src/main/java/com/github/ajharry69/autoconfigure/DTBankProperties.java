package com.github.ajharry69.autoconfigure;

import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "application.config")
@Validated
public record DTBankProperties(Zipkin zipkin, RabbitMq rabbitMq, Gateway gateway) {
    record RabbitMq(boolean enabled) {
    }

    record Zipkin(boolean enabled) {
    }

    @Validated
    record Gateway(@URL(regexp = ".*[^/]$") String url) {
    }
}
