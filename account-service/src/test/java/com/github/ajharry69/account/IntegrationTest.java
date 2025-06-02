package com.github.ajharry69.account;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;

import static io.restassured.RestAssured.given;

public abstract class IntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(IntegrationTest.class);
    private static final String TEST_CLIENT_ID = "test-client";
    private static final String TEST_CLIENT_SECRET = "test-secret";

    @Autowired
    protected OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

    protected String getAdminAccessToken() {
        return getAccessToken("adminuser", "adminpass");
    }

    protected String getAccessToken() {
        return getAccessToken("testuser", "password");
    }

    protected String getAccessToken(String username, String password) {
        String tokenEndpoint = oAuth2ResourceServerProperties.getJwt().getIssuerUri() + "/protocol/openid-connect/token";
        log.debug("Requesting access token for user '{}', client '{}' from endpoint: {}", username, TEST_CLIENT_ID, tokenEndpoint);

        try {
            RequestSpecification requestSpecification = given()
                    .contentType(ContentType.URLENC)
                    .formParam("grant_type", "password")
                    .formParam("client_id", TEST_CLIENT_ID)
                    .formParam("client_secret", TEST_CLIENT_SECRET)
                    .formParam("username", username)
                    .formParam("password", password)
                    .formParam("scope", "openid account.create account.read account.update account.delete");
            requestSpecification.log();
            Response response = requestSpecification
                    .when()
                    .post(tokenEndpoint);
            response.prettyPrint();
            String accessToken = response
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .extract()
                    .path("access_token");

            if (accessToken == null || accessToken.isBlank()) {
                log.error("Extracted access token is null or blank for user '{}', client '{}'", username, TEST_CLIENT_ID);
                throw new RuntimeException("Extracted access token was null or blank.");
            }

            log.debug("Successfully obtained access token for user '{}'", username);
            return accessToken;
        } catch (Exception e) {
            log.error("Failed to obtain access token for user '{}', client '{}' from {}: {}", username, TEST_CLIENT_ID, tokenEndpoint, e.getMessage(), e);
            throw new RuntimeException("Could not get Keycloak access token using password grant", e);
        }
    }
}
