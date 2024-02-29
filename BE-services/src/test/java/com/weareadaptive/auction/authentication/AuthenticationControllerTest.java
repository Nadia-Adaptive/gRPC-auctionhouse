package com.weareadaptive.auction.authentication;

import com.weareadaptive.auction.user.UserRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static com.weareadaptive.auction.TestData.generateJSON;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @LocalServerPort
    private int port;
    private String uri;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void beforeEach() {
        uri = "http://localhost:" + port + "/auth/";
    }

    @Test
    @DisplayName("POST /login provided valid credentials returns a JWT token")
    public void postLogin() {
        // @formatter:off
        given()
                .baseUri(uri)
                .header("Content-Type", "application/json")
        .when()
                .body(generateJSON("username", "admin", "password", "admin"))
                .post("login")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.username", containsString("admin"),
                        "data.role", containsString(UserRole.ADMIN.name()));
        // @formatter:on
    }

    @Test
    @DisplayName("POST auth/login with invalid credentials returns a message")
    public void postLoginWithInvalidCredentials() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header("Content-Type", "application/json")
        .when()
                .body(generateJSON("password", "ADMIN","password", "pass"))
                .post("login")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
        //@formatter:on
    }
}
