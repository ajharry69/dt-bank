package com.github.ajharry69.card;

import com.github.ajharry69.card.models.Card;
import com.github.ajharry69.card.models.CardCreateRequest;
import com.github.ajharry69.card.models.CardType;
import com.github.ajharry69.card.models.CardUpdateRequest;
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
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CardControllerTest {
    private static final Faker faker = new Faker();
    @Autowired
    private CardRepository repository;
    private Card card;

    private static String alias() {
        return faker.funnyName().name();
    }

    private static String pan() {
        return faker.finance().creditCard()
                .replaceAll("\\D", "");
    }

    private static String cvv() {
        return faker.expression("#{numerify '###'}");
    }

    private static CardType cardType() {
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
        card = repository.save(
                Card.builder()
                        .alias(alias())
                        .pan(pan())
                        .cvv(cvv())
                        .type(cardType())
                        .accountId(UUID.randomUUID())
                        .build()
        );
    }

    @Nested
    @DisplayName(value = "POST - /api/v1/cards")
    class CreateCard {
        @Test
        void shouldCreateCard() {
            String alias = alias();
            String pan = pan();
            String cvv = cvv();
            CardType type = cardType();
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(
                            CardCreateRequest.builder()
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
                    .body("pan", equalTo(pan))
                    .body("cvv", equalTo(cvv))
                    .body("type", equalTo(type.name()))
                    .body("accountId", notNullValue());
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
                    .contentType(ContentType.JSON)
                    .body(
                            CardCreateRequest.builder()
                                    .alias(alias)
                                    .pan(pan)
                                    .cvv(cvv())
                                    .type(cardType())
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
        @Test
        void shouldUpdateCard() {
            String alias = alias();
            String pan = pan();
            String cvv = cvv();
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(
                            CardUpdateRequest.builder()
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
                    .body("pan", equalTo(pan))
                    .body("cvv", equalTo(cvv))
                    .body("type", equalTo(card.getType().name()))
                    .body("accountId", notNullValue());
        }

        @Test
        void shouldReturnNotFoundForInvalidCardId() {
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(
                            CardUpdateRequest.builder()
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
                    .contentType(ContentType.JSON)
                    .body(
                            CardUpdateRequest.builder()
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
        @Test
        void shouldReturnCard() {
            Response response = given()
                    .when()
                    .get("/api/v1/cards/{cardId}", validCardDetailRequest());

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(String.valueOf(card.getId())))
                    .body("alias", equalTo(card.getAlias()))
                    .body("pan", equalTo(card.getPan()))
                    .body("cvv", equalTo(card.getCvv()))
                    .body("type", equalTo(card.getType().name()))
                    .body("accountId", equalTo(card.getAccountId().toString()));
        }

        @Test
        void shouldReturnNotFoundForInvalidCardId() {
            Response response = given()
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
        @Test
        void shouldReturnCard() {
            Response response = given()
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
                    .when()
                    .delete("/api/v1/cards/{cardId}", invalidCardDetailRequest());

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    @DisplayName(value = "GET - /api/v1/cards")
    class GetCards {
        @Test
        void shouldReturnCards() {
            Response response = given()
                    .when()
                    .get("/api/v1/cards");

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", equalTo(1));
        }
    }
}