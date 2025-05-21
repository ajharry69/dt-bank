package com.github.ajharry69.account.service.account;

import com.github.ajharry69.account.OptionalTestcontainersConfiguration;
import com.github.ajharry69.account.TestcontainersConfiguration;
import com.github.ajharry69.account.service.account.data.AccountFilter;
import com.github.ajharry69.account.service.account.data.AccountRepository;
import com.github.ajharry69.account.service.account.models.Account;
import com.github.ajharry69.account.service.account.models.dtos.AccountRequest;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

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

@Import({TestcontainersConfiguration.class, OptionalTestcontainersConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "spring.profiles.active=test")
class AccountControllerTest {
    private static final Faker faker = new Faker();
    private static final String iban = iban();
    private static final String bicSwift = bicSwift();
    @Autowired
    private AccountRepository repository;
    private Account account;

    private @NotNull HashMap<String, Object> validAccountDetailRequest() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("accountId", account.getId());
        return map;
    }

    private @NotNull HashMap<String, Object> invalidAccountDetailRequest() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("accountId", UUID.randomUUID());
        return map;
    }

    private static String iban() {
        return faker.finance().iban();
    }

    private static String bicSwift() {
        return faker.finance().bic();
    }

    @BeforeEach
    public void setUp() {
        RestAssured.port = RestAssured.DEFAULT_PORT;

        repository.deleteAll();
        repository.save(
                Account.builder()
                        .iban(iban)
                        .bicSwift(bicSwift)
                        .customerId(UUID.randomUUID())
                        .build()
        );
        account = repository.save(
                Account.builder()
                        .iban(iban())
                        .bicSwift(bicSwift())
                        .customerId(UUID.randomUUID())
                        .build()
        );
    }

    @Nested
    @DisplayName(value = "POST - /api/v1/accounts")
    class CreateAccount {
        @Test
        void shouldCreateAccount() {
            String iban = iban();
            String bicSwift = bicSwift();
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(
                            AccountRequest.builder()
                                    .iban(iban)
                                    .bicSwift(bicSwift)
                                    .customerId(UUID.randomUUID())
                                    .build()
                    )
                    .post("/api/v1/accounts");

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", not(emptyOrNullString()))
                    .body("iban", equalTo(iban))
                    .body("bicSwift", equalTo(bicSwift))
                    .body("customerId", notNullValue());
        }

        @ParameterizedTest
        @CsvSource(
                value = {
                        "' ',",
                        "null,null"
                },
                nullValues = {"null"}
        )
        void shouldReturnBadRequestForInvalidAccount(String iban, String bicSwift) {
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(
                            AccountRequest.builder()
                                    .iban(iban)
                                    .bicSwift(bicSwift)
                                    .customerId(UUID.randomUUID())
                                    .build()
                    )
                    .post("/api/v1/accounts");

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @DisplayName(value = "PUT - /api/v1/accounts/{accountId}")
    class UpdateAccount {
        @Test
        void shouldUpdateAccount() {
            String iban = iban();
            String bicSwift = bicSwift();
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(
                            AccountRequest.builder()
                                    .iban(iban)
                                    .bicSwift(bicSwift)
                                    .customerId(UUID.randomUUID())
                                    .build()
                    )
                    .put("/api/v1/accounts/{accountId}", validAccountDetailRequest());

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", allOf(not(emptyOrNullString()), equalTo(String.valueOf(account.getId()))))
                    .body("iban", equalTo(iban))
                    .body("bicSwift", equalTo(bicSwift))
                    .body("customerId", notNullValue());
        }

        @Test
        void shouldReturnNotFoundForInvalidAccountId() {
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(
                            AccountRequest.builder()
                                    .iban(iban())
                                    .bicSwift(bicSwift())
                                    .customerId(UUID.randomUUID())
                                    .build()
                    )
                    .put("/api/v1/accounts/{accountId}", invalidAccountDetailRequest());

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
        void shouldReturnBadRequestForInvalidAccount(String iban, String bicSwift) {
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(
                            AccountRequest.builder()
                                    .iban(iban)
                                    .bicSwift(bicSwift)
                                    .customerId(UUID.randomUUID())
                                    .build()
                    )
                    .put("/api/v1/accounts/{accountId}", validAccountDetailRequest());

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @DisplayName(value = "GET - /api/v1/accounts/{accountId}")
    class GetAccount {
        @Test
        void shouldReturnAccount() {
            Response response = given()
                    .when()
                    .get("/api/v1/accounts/{accountId}", validAccountDetailRequest());

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(String.valueOf(account.getId())))
                    .body("iban", equalTo(account.getIban()))
                    .body("bicSwift", equalTo(account.getBicSwift()))
                    .body("customerId", equalTo(account.getCustomerId().toString()));
        }

        @Test
        void shouldReturnNotFoundForInvalidAccountId() {
            Response response = given()
                    .when()
                    .get("/api/v1/accounts/{accountId}", invalidAccountDetailRequest());

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    @DisplayName(value = "DELETE - /api/v1/accounts/{accountId}")
    class DeleteAccount {
        @Test
        void shouldReturnAccount() {
            Response response = given()
                    .when()
                    .delete("/api/v1/accounts/{accountId}", validAccountDetailRequest());

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value())
                    .body(anyOf(emptyString(), blankString()));
        }

        @Test
        void shouldReturnNotFoundForInvalidAccountId() {
            Response response = given()
                    .when()
                    .delete("/api/v1/accounts/{accountId}", invalidAccountDetailRequest());

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    @DisplayName(value = "GET - /api/v1/accounts")
    class GetAccounts {
        static Stream<Arguments> shouldReturnAccounts() {
            return Stream.of(
                    arguments(
                            AccountFilter.builder()
                                    .build(),
                            5
                    ),
                    arguments(
                            AccountFilter.builder()
                                    .iban(iban)
                                    .build(),
                            1
                    ),
                    arguments(
                            AccountFilter.builder()
                                    .bicSwift(bicSwift)
                                    .build(),
                            1
                    ),
                    arguments(
                            AccountFilter.builder()
                                    .iban(faker.funnyName().name())
                                    .build(),
                            0
                    ),
                    arguments(
                            AccountFilter.builder()
                                    .startDateCreated(LocalDate.now().minusYears(3))
                                    .build(),
                            4
                    ),
                    arguments(
                            AccountFilter.builder()
                                    .endDateCreated(LocalDate.now().minusYears(4).plusDays(1))
                                    .build(),
                            1
                    ),
                    arguments(
                            AccountFilter.builder()
                                    .startDateCreated(LocalDate.now().minusYears(4))
                                    .endDateCreated(LocalDate.now().minusYears(2).plusDays(1))
                                    .build(),
                            3
                    ),
                    arguments(
                            AccountFilter.builder()
                                    .iban(iban)
                                    .startDateCreated(LocalDate.now().minusYears(4))
                                    .endDateCreated(LocalDate.now().minusYears(2).plusDays(1))
                                    .build(),
                            0
                    ),
                    arguments(
                            AccountFilter.builder()
                                    .bicSwift(bicSwift)
                                    .startDateCreated(LocalDate.now().minusYears(4))
                                    .endDateCreated(LocalDate.now().minusYears(2).plusDays(1))
                                    .build(),
                            0
                    )
            );
        }

        private static @NotNull Map<String, Object> queryParams(AccountFilter filter) {
            Map<String, Object> map = new HashMap<>();
            if (filter == null) {
                return map;
            }

            if (filter.iban() != null) {
                map.put("iban", filter.iban());
            }
            if (filter.bicSwift() != null) {
                map.put("bicSwift", filter.bicSwift());
            }
            if (filter.startDateCreated() != null) {
                map.put("startDateCreated", filter.startDateCreated().format(DateTimeFormatter.ISO_DATE));
            }
            if (filter.endDateCreated() != null) {
                map.put("endDateCreated", filter.endDateCreated().format(DateTimeFormatter.ISO_DATE));
            }
            return map;
        }

        @ParameterizedTest
        @MethodSource
        void shouldReturnAccounts(AccountFilter filter, int expectedTotalElements) {
            List.of(
                    OffsetDateTime.now().minusYears(4),
                    OffsetDateTime.now().minusYears(3),
                    OffsetDateTime.now().minusYears(2)
            ).forEach(dateTime -> {
                var account = repository.save(
                        Account.builder()
                                .iban(iban())
                                .bicSwift(bicSwift())
                                .customerId(UUID.randomUUID())
                                .build()
                );
                repository.updateDateCreatedById(dateTime, account.getId());
            });

            Response response = given()
                    .when()
                    .queryParams(queryParams(filter))
                    .get("/api/v1/accounts");

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", equalTo(expectedTotalElements));
        }
    }
}