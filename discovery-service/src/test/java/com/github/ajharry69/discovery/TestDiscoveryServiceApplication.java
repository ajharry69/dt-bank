package com.github.ajharry69.discovery;

import org.springframework.boot.SpringApplication;

public class TestDiscoveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(DiscoveryServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
