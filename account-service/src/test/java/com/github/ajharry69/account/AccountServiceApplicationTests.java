package com.github.ajharry69.account;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import({TestcontainersConfiguration.class})
@SpringBootTest(properties = "spring.profiles.active=test")
class AccountServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
