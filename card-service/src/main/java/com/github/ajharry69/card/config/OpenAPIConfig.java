package com.github.ajharry69.card.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
            @Value("${application.config.gateway.url:http://localhost:8080}") String gatewayUrl
    ) {
        var name = appName.replace("-service", "");
        name = name.replaceFirst("\\w", String.valueOf(Character.toUpperCase(name.charAt(0))));
        return new OpenAPI()
                .info(new Info()
                        .title(name + " API")
                        .version("v1")
                        .description("API documentation for " + name))
                .servers(List.of(new Server().url(gatewayUrl)));
    }
}
