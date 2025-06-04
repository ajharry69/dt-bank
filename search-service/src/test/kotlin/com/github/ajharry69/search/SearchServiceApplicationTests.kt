package com.github.ajharry69.search

import dasniko.testcontainers.keycloak.KeycloakContainer
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@Testcontainers
class SearchServiceApplicationTests {

    companion object {
        @Container
        @JvmStatic
        val keycloak: KeycloakContainer = KeycloakContainer()
            .withRealmImportFile("/realm.json")

        @DynamicPropertySource
        @JvmStatic
        fun keycloakProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri") {
                keycloak.authServerUrl + "/realms/dt-bank"
            }
        }
    }

    @Test
    fun contextLoads() {
    }

}
