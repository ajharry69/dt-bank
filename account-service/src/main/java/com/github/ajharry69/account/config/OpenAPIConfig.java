package com.github.ajharry69.account.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(version = "v1"),
        servers = {
                @Server(
                        description = "Development",
                        url = "http://localhost:8080"
                ),
        }
)
public class OpenAPIConfig {
}
