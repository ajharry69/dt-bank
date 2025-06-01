package com.github.ajharry69.autoconfigure;

import com.github.ajharry69.exceptions.DTBException;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
@EnableConfigurationProperties(DTBankProperties.class)
@ComponentScan(basePackageClasses = {DTBException.class})
class DTBAutoConfiguration {
    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
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
                .components(
                        new Components().addSecuritySchemes(
                                "OAuth2",
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
