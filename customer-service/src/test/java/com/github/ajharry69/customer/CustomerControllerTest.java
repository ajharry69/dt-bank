package com.github.ajharry69.customer;

import com.github.ajharry69.customer.models.Customer;
import com.github.ajharry69.customer.models.CustomerRequest;
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
class CustomerControllerTest {
    private static final Faker faker = new Faker();
    @Autowired
    private CustomerRepository repository;
    private Customer customer;

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

    private static String firstName() {
        return faker.name().firstName();
    }

    private static String lastName() {
        return faker.name().lastName();
    }

    @BeforeEach
    public void setUp() {
        RestAssured.port = RestAssured.DEFAULT_PORT;

        repository.deleteAll();
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
        @Test
        void shouldCreateCustomer() {
            String firstName = firstName();
            String lastName = lastName();
            Response response = given()
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
        @Test
        void shouldUpdateCustomer() {
            String firstName = firstName();
            String lastName = lastName();
            Response response = given()
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
        @Test
        void shouldReturnCustomer() {
            Response response = given()
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
                    .when()
                    .delete("/api/v1/customers/{customerId}", invalidCustomerDetailRequest());

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    @DisplayName(value = "GET - /api/v1/customers")
    class GetCustomers {
        @Test
        void shouldReturnCustomers() {
            Response response = given()
                    .when()
                    .get("/api/v1/customers");

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", equalTo(1));
        }
    }
}