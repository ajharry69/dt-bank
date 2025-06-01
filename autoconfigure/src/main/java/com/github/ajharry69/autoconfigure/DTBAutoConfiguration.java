package com.github.ajharry69.autoconfigure;

import com.github.ajharry69.exceptions.DTBException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DTBankProperties.class)
@ComponentScan(basePackageClasses = {DTBException.class})
class DTBAutoConfiguration {
}
