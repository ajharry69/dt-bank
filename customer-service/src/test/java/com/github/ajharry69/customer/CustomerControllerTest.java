package com.github.ajharry69.customer;

import com.github.ajharry69.customer.models.Customer;
import com.github.ajharry69.customer.models.CustomerRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CustomerControllerTest {
    @Autowired
    private CustomerRepository repository;

    @BeforeEach
    public void setUp() {
        RestAssured.port = RestAssured.DEFAULT_PORT;

        repository.deleteAll();
    }

    @Nested
    class CreateCustomer {
        @Test
        void shouldCreateCustomer() {
            Response response = given()
                    .contentType(ContentType.JSON)
                    .body(
                            CustomerRequest.builder()
                                    .firstName("First")
                                    .lastName("Last").build()
                    )
                    .post("/api/v1/customers");

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.CREATED.value());
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
                    .post("/api/v1/customers");

            response.prettyPrint();

            response
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class GetCustomers {
        @Test
        void shouldReturnCustomers() {
            repository.save(
                    Customer.builder()
                            .firstName("First")
                            .lastName("Last")
                            .build()
            );

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