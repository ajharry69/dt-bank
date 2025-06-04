package com.github.ajharry69.autoconfigure;

import com.github.ajharry69.SecuritySchemeName;
import com.github.ajharry69.exceptions.DTBAccessDeniedException;
import com.github.ajharry69.exceptions.DTBAuthenticationFailedException;
import com.github.ajharry69.exceptions.DTBException;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
@EnableConfigurationProperties(DTBankProperties.class)
@ComponentScan(basePackageClasses = {DTBException.class})
class DTBAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(DTBAutoConfiguration.class);

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.requestMatchers(
                                        "/actuator/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**",
                                        "/*/v3/api-docs/**"
                                ).permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(new OAuth2JwtAuthenticationTokenConverter())))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(
                                (x, y, ex) -> {
                                    log.debug("Access Denied: {}", ex.getMessage(), ex);
                                    throw new DTBAccessDeniedException();
                                }
                        )
                        .authenticationEntryPoint(
                                (x, y, ex) -> {
                                    log.debug("Authentication failed: {}", ex.getMessage(), ex);
                                    throw new DTBAuthenticationFailedException();
                                }
                        )
                )
                .build();
    }

    @Bean
    public OpenAPI openAPI(
            DTBankProperties properties,
            @Value("${spring.application.name}") String appName,
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:http://localhost}") String jwtIssuerUrl
    ) {
        var name = appName.replace("-service", "");
        name = name.replaceFirst("\\w", String.valueOf(Character.toUpperCase(name.charAt(0))));
        var openIdConnectUrl = jwtIssuerUrl + "/.well-known/openid-configuration";
        var gateway = properties.gateway();
        var url = gateway != null ? gateway.url() : "http://localhost:8080";

        return new OpenAPI()
                .info(new Info()
                        .title(name + " API")
                        .version("v1")
                        .description("API documentation for " + name))
                .servers(List.of(new Server().url(url)))
                .addSecurityItem(new SecurityRequirement().addList(SecuritySchemeName.OAUTH2))
                .components(
                        new Components().addSecuritySchemes(
                                SecuritySchemeName.OAUTH2,
                                new SecurityScheme()
                                        .in(SecurityScheme.In.HEADER)
                                        .type(SecurityScheme.Type.OPENIDCONNECT)
                                        .scheme("Bearer")
                                        .bearerFormat("JWT")
                                        .openIdConnectUrl(openIdConnectUrl)
                        )
                );
    }
}
