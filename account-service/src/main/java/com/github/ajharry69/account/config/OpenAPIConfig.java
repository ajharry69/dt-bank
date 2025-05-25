package com.github.ajharry69.account.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI openAPI(
            @Value("${spring.application.name}") String appName,
            @Value("${application.config.gateway.url:http://localhost:8080}") String gatewayUrl,
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:http://localhost}") String jwtIssuerUrl
    ) {
        var name = appName.replace("-service", "");
        name = name.replaceFirst("\\w", String.valueOf(Character.toUpperCase(name.charAt(0))));
        String openIdConnectUrl = jwtIssuerUrl + "/.well-known/openid-configuration";
        return new OpenAPI()
                .info(new Info()
                        .title(name + " API")
                        .version("v1")
                        .description("API documentation for " + name))
                .servers(List.of(new Server().url(gatewayUrl)))
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
