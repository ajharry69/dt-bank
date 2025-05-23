package com.github.ajharry69.customer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import({TestcontainersConfiguration.class})
@SpringBootTest(properties = "spring.profiles.active=test")
class CustomerServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
