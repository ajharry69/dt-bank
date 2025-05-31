package com.github.ajharry69.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DTBankProperties.class)
class DTBAutoConfiguration {
}
