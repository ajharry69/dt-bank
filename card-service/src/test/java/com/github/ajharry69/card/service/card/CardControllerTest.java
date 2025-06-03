package com.github.ajharry69.card.service.card;

import com.github.ajharry69.card.IntegrationTest;
import com.github.ajharry69.card.TestcontainersConfiguration;
import com.github.ajharry69.card.service.card.data.CardFilter;
import com.github.ajharry69.card.service.card.data.CardRepository;
import com.github.ajharry69.card.service.card.models.Card;
import com.github.ajharry69.card.service.card.models.CardType;
import com.github.ajharry69.card.service.card.models.dtos.CreateCardRequest;
import com.github.ajharry69.card.service.card.models.dtos.UpdateCardRequest;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@Import({TestcontainersConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles(value = {"test"})
@Testcontainers
class CardControllerTest extends IntegrationTest {
    private static final Faker faker = new Faker();
    @Container
    static KeycloakContainer keycloak = new KeycloakContainer()
            .withRealmImportFile("/realm.json");
    @Autowired
    private CardRepository repository;
    private Card card;

    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/dt-bank"
        );
    }

    private static String alias() {
        return faker.funnyName().name();
    }

    private static String pan() {
        return faker.finance().creditCard();
    }

    private static String cvv() {
        return faker.expression("#{numerify '###'}");
    }

    private static CardType type() {
        return faker.options().option(CardType.class);
    }

    private @NotNull HashMap<String, Object> validCardDetailRequest() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("cardId", card.getId());
        return map;
    }

    private @NotNull HashMap<String, Object> invalidCardDetailRequest() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("cardId", UUID.randomUUID());
        return map;
    }

    @BeforeEach
    public void setUp() {
        RestAssured.port = RestAssured.DEFAULT_PORT;

        repository.deleteAll();
        repository.save(
                Card.builder()
                        .alias("John Doe")
                        .pan(pan())
                        .cvv(cvv())
                        .type(type())
                        .accountId(UUID.randomUUID())
                        .build()
        );
        card = repository.save(
                Card.builder()
                        .alias(alias())
                        .pan(pan())
                        .cvv(cvv())
                        .type(type())
                        .accountId(UUID.randomUUID())
                        .build()
        );
    }

    @Nested
    @DisplayName(value = "POST - /api/v1/cards")
    class CreateCard {
        @Nested
        class AccessDenied {
            @Test
            void shouldDeny() {
                String alias = alias();
                String pan = pan();
                String cvv = cvv();
                CardType type = type();
                Response response = given()
                        .auth().oauth2(getAccessToken())
                        .contentType(ContentType.JSON)
                        .body(
                                CreateCardRequest.builder()
                                        .alias(alias)
                                        .pan(pan)
                                        .cvv(cvv)
                                        .type(type)
                                        .accountId(UUID.randomUUID())
                                        .build()
                        )
                        .post("/api/v1/cards");

                response.prettyPrint();

                response
                        .then()
                        .statusCode(HttpStatus.FORBIDDEN.value())
                        .body("errorCode", equalTo("ACCESS_DENIED"));
            }
        }

        @Nested
        class AccessGranted {
            @Test
            void shouldCreateCard() {
                String alias = alias();
                String pan = pan();
                String cvv = cvv();
                CardType type = type();
                Response response = given()
                        .auth().oauth2(getAdminAccessToken())
                        .contentType(ContentType.JSON)
                        .body(
                                CreateCardRequest.builder()
                                        .alias(alias)
                                        .pan(pan)
                                        .cvv(cvv)
                                        .type(type)
                                        .accountId(UUID.randomUUID())
                                        .build()
                        )
                        .post("/api/v1/cards");

                response.prettyPrint();

                response
                        .then()
                        .statusCode(HttpStatus.CREATED.value())
                        .body("id", not(emptyOrNullString()))
                        .body("alias", equalTo(alias))
                        .body("pan", equalTo("*************"))
                        .body("cvv", equalTo("***"))
                        .body("type", equalTo(type.name()))
                        .body("accountId", notNullValue());
            }
        }

        @ParameterizedTest
        @CsvSource(
                value = {
                        "' ',",
                        "null,null"
                },
                nullValues = {"null"}
        )
        void shouldReturnBadRequestForInvalidCard(String alias, String pan) {
            Response response = given()
                    .auth().oauth2(getAccessToken())
                    .contentType(ContentType.JSON)
                    .body(
                            CreateCardRequest.builder()
                                    .alias(alias)
                                    .pan(pan)
                                    .cvv(cvv())
                                    .type(type())
                                    .accountId(UUID.randomUUID())
                                    .build()
                    )
                    .post("/api/v1/cards");

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @DisplayName(value = "PUT - /api/v1/cards/{cardId}")
    class UpdateCard {
        @Nested
        class AccessDenied {
            @Test
            void shouldDeny() {
                String alias = alias();
                String pan = pan();
                String cvv = cvv();
                Response response = given()
                        .auth().oauth2(getAccessToken())
                        .contentType(ContentType.JSON)
                        .body(
                                UpdateCardRequest.builder()
                                        .alias(alias)
                                        .pan(pan)
                                        .cvv(cvv)
                                        .build()
                        )
                        .put("/api/v1/cards/{cardId}", validCardDetailRequest());

                response.prettyPrint();

                response
                        .then()
                        .statusCode(HttpStatus.FORBIDDEN.value())
                        .body("errorCode", equalTo("ACCESS_DENIED"));
            }
        }

        @Nested
        class AccessGranted {
            @Test
            void shouldUpdateCard() {
                String alias = alias();
                String pan = pan();
                String cvv = cvv();
                Response response = given()
                        .auth().oauth2(getAdminAccessToken())
                        .contentType(ContentType.JSON)
                        .body(
                                UpdateCardRequest.builder()
                                        .alias(alias)
                                        .pan(pan)
                                        .cvv(cvv)
                                        .build()
                        )
                        .put("/api/v1/cards/{cardId}", validCardDetailRequest());

                response.prettyPrint();

                response
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .body("id", allOf(not(emptyOrNullString()), equalTo(String.valueOf(card.getId()))))
                        .body("alias", equalTo(alias))
                        .body("pan", equalTo("*************"))
                        .body("cvv", equalTo("***"))
                        .body("type", equalTo(card.getType().name()))
                        .body("accountId", notNullValue());
            }

            @Test
            void shouldReturnNotFoundForInvalidCardId() {
                Response response = given()
                        .auth().oauth2(getAdminAccessToken())
                        .contentType(ContentType.JSON)
                        .body(
                                UpdateCardRequest.builder()
                                        .alias(alias())
                                        .pan(pan())
                                        .cvv(cvv())
                                        .build()
                        )
                        .put("/api/v1/cards/{cardId}", invalidCardDetailRequest());

                response.prettyPrint();

                response
                        .then()
                        .statusCode(HttpStatus.NOT_FOUND.value());
            }
        }

        @ParameterizedTest
        @CsvSource(
                value = {
                        "' ',",
                        "null,null"
                },
                nullValues = {"null"}
        )
        void shouldReturnBadRequestForInvalidCard(String alias, String pan) {
            Response response = given()
                    .auth().oauth2(getAccessToken())
                    .contentType(ContentType.JSON)
                    .body(
                            UpdateCardRequest.builder()
                                    .alias(alias)
                                    .pan(pan)
                                    .cvv(cvv())
                                    .build()
                    )
                    .put("/api/v1/cards/{cardId}", validCardDetailRequest());

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @DisplayName(value = "GET - /api/v1/cards/{cardId}")
    class GetCard {
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void shouldReturnCard(boolean unmask) {
            Response response = given()
                    .auth().oauth2(getAccessToken())
                    .when()
                    .get("/api/v1/cards/{cardId}", validCardDetailRequest());

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(String.valueOf(card.getId())))
                    .body("alias", equalTo(card.getAlias()))
                    .body("pan", equalTo("*************"))
                    .body("cvv", equalTo("***"))
                    .body("type", equalTo(card.getType().name()))
                    .body("accountId", equalTo(card.getAccountId().toString()));
        }

        @Test
        void shouldReturnNotFoundForInvalidCardId() {
            Response response = given()
                    .auth().oauth2(getAccessToken())
                    .when()
                    .get("/api/v1/cards/{cardId}", invalidCardDetailRequest());

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    @DisplayName(value = "DELETE - /api/v1/cards/{cardId}")
    class DeleteCard {
        @Nested
        class AccessDenied {
            @Test
            void shouldDeny() {
                Response response = given()
                        .auth().oauth2(getAccessToken())
                        .when()
                        .delete("/api/v1/cards/{cardId}", validCardDetailRequest());

                response.prettyPrint();

                response
                        .then()
                        .statusCode(HttpStatus.FORBIDDEN.value())
                        .body("errorCode", equalTo("ACCESS_DENIED"));
            }
        }

        @Nested
        class AccessGranted {
            @Test
            void shouldReturnCard() {
                Response response = given()
                        .auth().oauth2(getAdminAccessToken())
                        .when()
                        .delete("/api/v1/cards/{cardId}", validCardDetailRequest());

                response.prettyPrint();

                response
                        .then()
                        .statusCode(HttpStatus.NO_CONTENT.value())
                        .body(anyOf(emptyString(), blankString()));
            }

            @Test
            void shouldReturnNotFoundForInvalidCardId() {
                Response response = given()
                        .auth().oauth2(getAdminAccessToken())
                        .when()
                        .delete("/api/v1/cards/{cardId}", invalidCardDetailRequest());

                response.prettyPrint();

                response
                        .then()
                        .statusCode(HttpStatus.NOT_FOUND.value());
            }
        }
    }

    @Nested
    @DisplayName(value = "GET - /api/v1/cards")
    class GetCards {
        static Stream<Arguments> shouldReturnCards() {
            return Stream.of(
                    arguments(
                            CardFilter.builder()
                                    .build(),
                            5
                    ),
                    arguments(
                            CardFilter.builder()
                                    .unmask(true)
                                    .build(),
                            5
                    ),
                    arguments(
                            CardFilter.builder()
                                    .alias("John")
                                    .build(),
                            1
                    ),
                    arguments(
                            CardFilter.builder()
                                    .alias("joHn")
                                    .build(),
                            1
                    ),
                    arguments(
                            CardFilter.builder()
                                    .alias(faker.name().name())
                                    .build(),
                            0
                    ),
                    arguments(
                            CardFilter.builder()
                                    .startDateCreated(LocalDate.now().minusYears(3))
                                    .build(),
                            4
                    ),
                    arguments(
                            CardFilter.builder()
                                    .endDateCreated(LocalDate.now().minusYears(4).plusDays(1))
                                    .build(),
                            1
                    ),
                    arguments(
                            CardFilter.builder()
                                    .startDateCreated(LocalDate.now().minusYears(4))
                                    .endDateCreated(LocalDate.now().minusYears(2).plusDays(1))
                                    .build(),
                            3
                    ),
                    arguments(
                            CardFilter.builder()
                                    .alias("JohN")
                                    .startDateCreated(LocalDate.now().minusYears(4))
                                    .endDateCreated(LocalDate.now().minusYears(2).plusDays(1))
                                    .build(),
                            0
                    )
            );
        }

        private static @NotNull Map<String, Object> queryParams(CardFilter filter) {
            Map<String, Object> map = new HashMap<>();
            if (filter == null) {
                return map;
            }

            if (filter.getUnmask() != null) {
                map.put("unmask", filter.getUnmask());
            }
            if (filter.getPan() != null) {
                map.put("pan", filter.getPan());
            }
            if (filter.getAlias() != null) {
                map.put("alias", filter.getAlias());
            }
            if (filter.getType() != null) {
                map.put("type", filter.getType());
            }
            if (filter.getStartDateCreated() != null) {
                map.put("startDateCreated", filter.getStartDateCreated().format(DateTimeFormatter.ISO_DATE));
            }
            if (filter.getEndDateCreated() != null) {
                map.put("endDateCreated", filter.getEndDateCreated().format(DateTimeFormatter.ISO_DATE));
            }
            return map;
        }

        @ParameterizedTest
        @MethodSource
        void shouldReturnCards(CardFilter filter, int expectedTotalElements) {
            List.of(
                    OffsetDateTime.now().minusYears(4),
                    OffsetDateTime.now().minusYears(3),
                    OffsetDateTime.now().minusYears(2)
            ).forEach(dateTime -> {
                var card = repository.save(
                        Card.builder()
                                .alias(alias())
                                .pan(pan())
                                .cvv(cvv())
                                .type(type())
                                .accountId(UUID.randomUUID())
                                .build()
                );
                repository.updateDateCreatedById(dateTime, card.getId());
            });

            Response response = given()
                    .auth().oauth2(getAccessToken())
                    .when()
                    .queryParams(queryParams(filter))
                    .get("/api/v1/cards");

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", equalTo(expectedTotalElements));
        }
    }
}