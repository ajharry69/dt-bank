package com.github.ajharry69.account.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Configuration
@Slf4j
public class FeignClientConfig {
    @Bean
    RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            final var securityContext = SecurityContextHolder.getContext();
            if (securityContext == null) {
                log.warn("Security context is null. Skipping further request interception...");
                return;
            }

            final var authentication = securityContext.getAuthentication();
            String authorizationHeaderValue;
            if (authentication != null) {
                log.info("Using authentication credentials from the security context...");
                authorizationHeaderValue = "Bearer " + authentication.getCredentials();
            } else {
                log.info("Falling back to authentication credentials from the request attributes...");
                var requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes == null) {
                    log.warn("Request attributes are null. Skipping further request interception...");
                    return;
                }

                var request = requestAttributes.getRequest();
                authorizationHeaderValue = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (authorizationHeaderValue == null) {
                    log.warn("Authorization header is null. Skipping further request interception...");
                    return;
                }
            }

            log.info("Setting Authorization header with value.");
            requestTemplate.header(HttpHeaders.AUTHORIZATION, authorizationHeaderValue);
        };
    }
}
