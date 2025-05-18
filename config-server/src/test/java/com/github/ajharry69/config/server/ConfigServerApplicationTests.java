package com.github.ajharry69.config.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ConfigServerApplicationTests {

    @Test
    void contextLoads() {
    }

}
