package com.github.ajharry69.customer.service.customer;

import com.github.ajharry69.customer.IntegrationTest;
import com.github.ajharry69.customer.TestcontainersConfiguration;
import com.github.ajharry69.customer.service.customer.data.CustomerFilter;
import com.github.ajharry69.customer.service.customer.data.CustomerRepository;
import com.github.ajharry69.customer.service.customer.models.Customer;
import com.github.ajharry69.customer.service.customer.models.dtos.CustomerRequest;
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
class CustomerControllerTest extends IntegrationTest {
    private static final Faker faker = new Faker();
    @Container
    static KeycloakContainer keycloak = new KeycloakContainer()
            .withRealmImportFile("/realm.json");
    @Autowired
    private CustomerRepository repository;
    private Customer customer;

    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/dt-bank"
        );
    }

    private static String firstName() {
        return faker.name().firstName();
    }

    private static String lastName() {
        return faker.name().lastName();
    }

    private @NotNull HashMap<String, Object> validCustomerDetailRequest() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("customerId", customer.getId());
        return map;
    }

    private @NotNull HashMap<String, Object> invalidCustomerDetailRequest() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("customerId", UUID.randomUUID());
        return map;
    }

    @BeforeEach
    public void setUp() {
        RestAssured.port = RestAssured.DEFAULT_PORT;

        repository.deleteAll();
        repository.save(
                Customer.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .build()
        );
        customer = repository.save(
                Customer.builder()
                        .firstName(firstName())
                        .lastName(lastName())
                        .build()
        );
    }

    @Nested
    @DisplayName(value = "POST - /api/v1/customers")
    class CreateCustomer {
        @Nested
        class AccessDenied {
            @Test
            void shouldDeny() {
                String firstName = firstName();
                String lastName = lastName();
                Response response = given()
                        .auth().oauth2(getAccessToken())
                        .contentType(ContentType.JSON)
                        .body(
                                CustomerRequest.builder()
                                        .firstName(firstName)
                                        .lastName(lastName).build()
                        )
                        .post("/api/v1/customers");

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
            void shouldCreateCustomer() {
                String firstName = firstName();
                String lastName = lastName();
                Response response = given()
                        .auth().oauth2(getAdminAccessToken())
                        .contentType(ContentType.JSON)
                        .body(
                                CustomerRequest.builder()
                                        .firstName(firstName)
                                        .lastName(lastName).build()
                        )
                        .post("/api/v1/customers");

                response.prettyPrint();

                response
                        .then()
                        .statusCode(HttpStatus.CREATED.value())
                        .body("id", not(emptyOrNullString()))
                        .body("firstName", equalTo(firstName))
                        .body("lastName", equalTo(lastName))
                        .body("otherName", nullValue());
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
        void shouldReturnBadRequestForInvalidCustomer(String firstName, String lastName) {
            Response response = given()
                    .auth().oauth2(getAccessToken())
                    .contentType(ContentType.JSON)
                    .body(
                            CustomerRequest.builder()
                                    .firstName(firstName)
                                    .lastName(lastName)
                                    .build()
                    )
                    .post("/api/v1/customers");

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @DisplayName(value = "PUT - /api/v1/customers/{customerId}")
    class UpdateCustomer {
        @Nested
        class AccessDenied {
            @Test
            void shouldDeny() {
                String firstName = firstName();
                String lastName = lastName();
                Response response = given()
                        .auth().oauth2(getAccessToken())
                        .contentType(ContentType.JSON)
                        .body(
                                CustomerRequest.builder()
                                        .firstName(firstName)
                                        .lastName(lastName)
                                        .build()
                        )
                        .put("/api/v1/customers/{customerId}", validCustomerDetailRequest());

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
            void shouldUpdateCustomer() {
                String firstName = firstName();
                String lastName = lastName();
                Response response = given()
                        .auth().oauth2(getAdminAccessToken())
                        .contentType(ContentType.JSON)
                        .body(
                                CustomerRequest.builder()
                                        .firstName(firstName)
                                        .lastName(lastName)
                                        .build()
                        )
                        .put("/api/v1/customers/{customerId}", validCustomerDetailRequest());

                response.prettyPrint();

                response
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .body("id", allOf(not(emptyOrNullString()), equalTo(String.valueOf(customer.getId()))))
                        .body("firstName", equalTo(firstName))
                        .body("lastName", equalTo(lastName))
                        .body("otherName", nullValue());
            }

            @Test
            void shouldReturnNotFoundForInvalidCustomerId() {
                Response response = given()
                        .auth().oauth2(getAdminAccessToken())
                        .contentType(ContentType.JSON)
                        .body(
                                CustomerRequest.builder()
                                        .firstName(firstName())
                                        .lastName(lastName())
                                        .build()
                        )
                        .put("/api/v1/customers/{customerId}", invalidCustomerDetailRequest());

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
        void shouldReturnBadRequestForInvalidCustomer(String firstName, String lastName) {
            Response response = given()
                    .auth().oauth2(getAccessToken())
                    .contentType(ContentType.JSON)
                    .body(CustomerRequest.builder().firstName(firstName).lastName(lastName).build())
                    .put("/api/v1/customers/{customerId}", validCustomerDetailRequest());

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @DisplayName(value = "GET - /api/v1/customers/{customerId}")
    class GetCustomer {
        @Test
        void shouldReturnCustomer() {
            Response response = given()
                    .auth().oauth2(getAccessToken())
                    .when()
                    .get("/api/v1/customers/{customerId}", validCustomerDetailRequest());

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("id", equalTo(String.valueOf(customer.getId())))
                    .body("firstName", equalTo(customer.getFirstName()))
                    .body("lastName", equalTo(customer.getLastName()))
                    .body("otherName", equalTo(customer.getOtherName()));
        }

        @Test
        void shouldReturnNotFoundForInvalidCustomerId() {
            Response response = given()
                    .auth().oauth2(getAccessToken())
                    .when()
                    .get("/api/v1/customers/{customerId}", invalidCustomerDetailRequest());

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    @DisplayName(value = "DELETE - /api/v1/customers/{customerId}")
    class DeleteCustomer {
        @Nested
        class AccessDenied {
            @Test
            void shouldDeny() {
                Response response = given()
                        .auth().oauth2(getAccessToken())
                        .when()
                        .delete("/api/v1/customers/{customerId}", validCustomerDetailRequest());

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
            void shouldReturnCustomer() {
                Response response = given()
                        .auth().oauth2(getAdminAccessToken())
                        .when()
                        .delete("/api/v1/customers/{customerId}", validCustomerDetailRequest());

                response.prettyPrint();

                response
                        .then()
                        .statusCode(HttpStatus.NO_CONTENT.value())
                        .body(anyOf(emptyString(), blankString()));
            }

            @Test
            void shouldReturnNotFoundForInvalidCustomerId() {
                Response response = given()
                        .auth().oauth2(getAdminAccessToken())
                        .when()
                        .delete("/api/v1/customers/{customerId}", invalidCustomerDetailRequest());

                response.prettyPrint();

                response
                        .then()
                        .statusCode(HttpStatus.NOT_FOUND.value());
            }
        }
    }

    @Nested
    @DisplayName(value = "GET - /api/v1/customers")
    class GetCustomers {
        static Stream<Arguments> shouldReturnCustomers() {
            return Stream.of(
                    arguments(
                            CustomerFilter.builder()
                                    .build(),
                            5
                    ),
                    arguments(
                            CustomerFilter.builder()
                                    .name(faker.name().name())
                                    .build(),
                            0
                    ),
                    arguments(
                            CustomerFilter.builder()
                                    .name(faker.name().name().toLowerCase())
                                    .build(),
                            0
                    ),
                    arguments(
                            CustomerFilter.builder()
                                    .name(faker.name().name().replaceAll("\\s+", " OR "))
                                    .build(),
                            0
                    ),
                    arguments(
                            CustomerFilter.builder()
                                    .name(faker.name().name().replaceAll("\\s+", " OR ").toUpperCase())
                                    .build(),
                            0
                    ),
                    arguments(
                            CustomerFilter.builder()
                                    .name("\"" + faker.name().name() + "\"")
                                    .build(),
                            0
                    ),
                    arguments(
                            CustomerFilter.builder()
                                    .startDateCreated(LocalDate.now().minusYears(3))
                                    .build(),
                            4
                    ),
                    arguments(
                            CustomerFilter.builder()
                                    .endDateCreated(LocalDate.now().minusYears(4).plusDays(1))
                                    .build(),
                            1
                    ),
                    arguments(
                            CustomerFilter.builder()
                                    .startDateCreated(LocalDate.now().minusYears(4))
                                    .endDateCreated(LocalDate.now().minusYears(2).plusDays(1))
                                    .build(),
                            3
                    )
            );
        }

        private static @NotNull Map<String, Object> queryParams(CustomerFilter filter) {
            Map<String, Object> map = new HashMap<>();
            if (filter == null) {
                return map;
            }

            if (filter.name() != null) {
                map.put("name", filter.name());
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
        void shouldReturnCustomers(CustomerFilter filter, int expectedTotalElements) {
            List.of(
                    OffsetDateTime.now().minusYears(4),
                    OffsetDateTime.now().minusYears(3),
                    OffsetDateTime.now().minusYears(2)
            ).forEach(dateTime -> {
                var customer = repository.save(
                        Customer.builder()
                                .firstName(firstName())
                                .lastName(lastName())
                                .build()
                );
                repository.updateDateCreatedById(dateTime, customer.getId());
            });

            Response response = given()
                    .auth().oauth2(getAccessToken())
                    .when()
                    .queryParams(queryParams(filter))
                    .get("/api/v1/customers");

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", equalTo(expectedTotalElements));
        }
    }
}