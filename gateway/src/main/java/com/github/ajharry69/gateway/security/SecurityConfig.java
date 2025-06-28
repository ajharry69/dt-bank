package com.github.ajharry69.gateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final ObjectMapper objectMapper;

    public SecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String errorCode) {
        exchange.getResponse()
                .setStatusCode(status);
        exchange.getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("timestamp", Instant.now());
        payload.put("status", status.value());
        payload.put("errorCode", errorCode);
        payload.put("path", exchange.getRequest().getPath().value());

        try {
            byte[] responseBytes = objectMapper.writeValueAsBytes(payload);
            var buffer = exchange.getResponse()
                    .bufferFactory()
                    .wrap(responseBytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error writing JSON error response", e);
            exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
            var buffer = exchange.getResponse()
                    .bufferFactory()
                    .wrap(status.getReasonPhrase().getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                "/favicon.ico",
                                "/actuator/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/*/v3/api-docs",
                                "/webjars/**"
                        )
                        .permitAll()
                        .anyExchange()
                        .authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .oauth2Login(Customizer.withDefaults())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(
                                (exchange, ex) -> {
                                    log.debug("Access Denied: {}", ex.getMessage());
                                    return writeErrorResponse(exchange, HttpStatus.FORBIDDEN, "ACCESS_DENIED");
                                }
                        )
                        .authenticationEntryPoint(
                                (exchange, ex) -> {
                                    log.debug("Authentication failed: {}", ex.getMessage());
                                    return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED");
                                }
                        )
                )
                .build();
    }
}