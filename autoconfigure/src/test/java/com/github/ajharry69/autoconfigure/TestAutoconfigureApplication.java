package com.github.ajharry69.autoconfigure;

import org.springframework.boot.SpringApplication;

public class TestAutoconfigureApplication {

	public static void main(String[] args) {
		SpringApplication.from(AutoconfigureApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
