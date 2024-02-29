package com.weareadaptive.auction.security;

import com.weareadaptive.auction.apiTestData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static com.weareadaptive.auction.apiTestData.adminAuthToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpHeaders.COOKIE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityTest {
    @Autowired
    apiTestData apiTestData;

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
    static void beforeAll(){
        postgres.start();
    }

    @AfterAll
    static void afterAll(){
        postgres.stop();
    }

    @BeforeEach
    public void initialiseRestAssuredMockMvcStandalone() {
        uri = "http://localhost:" + port + "/api/v1";
    }

    @Test
    public void shouldBeUnauthorizedWhenNotAuthenticated() {
        //@formatter:off
        given()
                .baseUri(uri)
         .when()
                .get("/test")
         .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
        //@formatter:on
    }

    @Test
    public void shouldBeAuthenticated() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header(COOKIE, adminAuthToken)
                .when()
                .get("/test")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("hour"));
        //@formatter:on
    }

    @Test
    public void shouldBeAnAdmin() {
        //@formatter:off
        given()
                .baseUri(uri)
        .when()
                .get("/test/adminOnly")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("super"));
        //@formatter:on
    }

    @Test
    public void shouldReturnForbiddenWhenNotAnAdmin() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header(COOKIE, apiTestData.user1Token())
        .when()
                .get("/test/adminOnly")
        .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
        //@formatter:on
    }

    @Test
    public void shouldReturnUnauthorizedWhenProvidedInvalidToken() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header(COOKIE, "invalid")
        .when()
                .get("/test/adminOnly")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
        //@formatter:on
    }
}
