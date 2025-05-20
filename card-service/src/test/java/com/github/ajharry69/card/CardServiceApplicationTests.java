package com.github.ajharry69.card;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import({TestcontainersConfiguration.class, OptionalTestcontainersConfiguration.class})
@SpringBootTest
class CardServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
