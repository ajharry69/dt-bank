package com.github.ajharry69.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}


@RestController
class FallbackController {

    private static final Logger log = LoggerFactory.getLogger(FallbackController.class);

    @GetMapping("/customers-fallback")
    Flux<Void> getCustomersFallback() {
        log.info("Fallback for customer service");
        return Flux.empty();
    }

    @GetMapping("/accounts-fallback")
    Flux<Void> getAccountsFallback() {
        log.info("Fallback for account service");
        return Flux.empty();
    }

    @GetMapping("/cards-fallback")
    Flux<Void> getCardsFallback() {
        log.info("Fallback for card service");
        return Flux.empty();
    }

}
