package com.weareadaptive.auction.organisation;

import com.weareadaptive.auction.apiTestData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static com.weareadaptive.auction.TestData.ADMIN_ORG;
import static com.weareadaptive.auction.TestData.ORG_1;
import static com.weareadaptive.auction.apiTestData.adminAuthToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.http.HttpHeaders.COOKIE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrganisationControllerTest {
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
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void beforeEach() {
        uri = "http://localhost:" + port + "/api/v1/organisations/";
    }

    @Test
    @DisplayName("GetAllOrganisations_ReturnsAllOrganisations")
    void getAllOrganisations() {
        //@formatter:off
        given().
                baseUri(uri)
                .header(COOKIE, adminAuthToken)
        .when()
                .get()
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", hasItems(2, 1),
                        "name", hasItems(ORG_1, ADMIN_ORG));
        //@formatter:on
    }

    @Test
    @DisplayName("GetOrganisation_ValidId_ReturnsOrganisation")
    void getOrganisation() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header(COOKIE, adminAuthToken)
        .when()
                .get("1")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(1),
                        "name", equalTo(ADMIN_ORG),
                        "users[0].username", equalTo("ADMIN"),
                        "users[0].userRole", equalTo("ADMIN")
                );
        //@formatter:on
    }

    @Test
    @DisplayName("GetOrganisation_InvalidId_ReturnsMessage")
    void getOrganisationInvalidId() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header(COOKIE, adminAuthToken)
        .when()
                .get("-1")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
        //@formatter:on
    }
}
