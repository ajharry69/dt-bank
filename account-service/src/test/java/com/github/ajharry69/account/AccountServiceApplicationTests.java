package com.github.ajharry69.account;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import({TestcontainersConfiguration.class, OptionalTestcontainersConfiguration.class})
@SpringBootTest
class AccountServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
