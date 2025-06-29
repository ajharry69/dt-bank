package com.github.ajharry69.gateway.security;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal()
            // If a principal exists, use its name as the key
            .map(principal -> Objects.requireNonNullElse(principal.getName(), "anonymous"))
            // If no principal exists (anonymous request), fall back to the remote IP address
            .defaultIfEmpty(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress());
    }
}