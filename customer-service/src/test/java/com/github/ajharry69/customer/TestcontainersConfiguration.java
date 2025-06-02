package com.github.ajharry69.customer;

import com.github.ajharry69.customer.service.customer.data.CustomerRepository;
import com.github.ajharry69.customer.service.customer.models.Customer;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {
    private static final Logger log = LoggerFactory.getLogger(TestcontainersConfiguration.class);
    private final Faker faker = new Faker();

    @Bean
    CommandLineRunner generateSampleData(final CustomerRepository repository) {
        return _ -> generateDataAsync(repository);
    }

    @Async
    public void generateDataAsync(CustomerRepository repository) {
        List<Customer> customers = new ArrayList<>();
        int numCustomers = new Random().nextInt(10, 100);
        log.info("Generating {} customers...", numCustomers);
        for (int i = 0; i < numCustomers; i++) {
            customers.add(
                    Customer.builder()
                            .firstName(faker.name().firstName())
                            .lastName(faker.name().lastName())
                            .build()
            );
        }
        log.info("Saving {} customers...", numCustomers);
        repository.saveAll(customers);
        log.info("Saved {} customers", numCustomers);
    }

    @Bean
    @RestartScope
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"));
    }

    @Bean
    @RestartScope
    @ServiceConnection(name = "redis")
    @ConditionalOnProperty(name = "application.config.redis.enabled", havingValue = "true")
    GenericContainer<?> redisContainer() {
        return new GenericContainer<>(DockerImageName.parse("redis:8.0-alpine")).withExposedPorts(6379);
    }

    @Bean
    @RestartScope
    @ServiceConnection
    @ConditionalOnProperty(name = "application.config.rabbitmq.enabled", havingValue = "true")
    RabbitMQContainer rabbitContainer() {
        return new RabbitMQContainer(DockerImageName.parse("rabbitmq:4.1-alpine"));
    }

    @Bean
    @RestartScope
    @ServiceConnection(name = "openzipkin/zipkin")
    @ConditionalOnProperty(name = "application.config.zipkin.enabled", havingValue = "true")
    GenericContainer<?> zipkinContainer() {
        return new GenericContainer<>(DockerImageName.parse("openzipkin/zipkin:3.5")).withExposedPorts(9411);
    }
}
