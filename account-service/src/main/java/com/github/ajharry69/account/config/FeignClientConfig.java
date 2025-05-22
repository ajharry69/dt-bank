package com.github.ajharry69.account.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Configuration
@Slf4j
public class FeignClientConfig {
    @Bean
    public RequestInterceptor forwardedHeaderInterceptor() {
        return template -> {
            log.info("Intercepting Forwarded headers...");
            var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                log.warn("Forwarded headers not present.");
                return;
            }
            var request = attributes.getRequest();
            List.of(
                    "X-Forwarded-For",
                    "X-Forwarded-Host",
                    "X-Forwarded-Port",
                    "X-Forwarded-Proto"
            ).forEach((headerName) -> {
                var headerValue = request.getHeader(headerName);
                if (headerValue != null) template.header(headerName, headerValue);
            });
            log.info("Forwarded headers intercepted for {}.", template.url());
        };
    }
}