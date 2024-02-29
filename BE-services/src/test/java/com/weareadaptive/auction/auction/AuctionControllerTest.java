package com.weareadaptive.auction.auction;

import com.weareadaptive.auction.apiTestData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;
import java.util.stream.Stream;

import static com.weareadaptive.auction.TestData.generateJSON;
import static com.weareadaptive.auction.apiTestData.adminAuthToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpHeaders.COOKIE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuctionControllerTest {
    @Autowired
    AuctionService service;

    @Autowired
    apiTestData apiData;
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @LocalServerPort
    private int port;
    protected String uri;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static protected void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static protected void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    protected void beforeEach() {
        uri = "http://localhost:" + port + "/api/v1/auctions/";
    }

    private static Map<String, String> generateInput(final String product, final String minPrice,
                                                     final String quantity) {
        return generateJSON("product", product, "minPrice", minPrice, "quantity", quantity);
    }

    private static Stream<Map<String, String>> invalidInput() {
        return Stream.of(
                generateInput("", "1.0", "10"),
                generateInput("TEST", "", "10"),
                generateInput("TEST", "1.0", ""));
    }

    @Test
    @DisplayName("when POST /auctions is provided valid json returns a message and 201 http code")
    public void postAuction() {
        // @formatter:off
        given()
                .baseUri(uri)
                .header("Content-Type", "application/json")
                .header(COOKIE, adminAuthToken)
        .when()
                .body(generateInput("test", "1.0", "10"))
                .post()
        .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("data.id", equalTo(2),
                        "data.ownerId", equalTo(3),
                        "data.product", equalTo("test"),
                        "data.quantity", equalTo(10),
                        "data.minPrice", equalTo(1.0f)
                        );
        // @formatter:on
    }

    @ParameterizedTest
    @MethodSource("invalidInput")
    @DisplayName("when POST /auctions is provided invalid input returns a message and 400 http code")
    public void postAuctionWithInvalidInput(final Map<String, String> input) {
        //@formatter:off
        given()
                .baseUri(uri)
                .header("Content-Type", "application/json")
                .header(COOKIE, adminAuthToken)
        .when()
                .body(input)
                .post()
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        //@formatter:on
    }

    @Test
    @DisplayName("when GET /auctions is provided a valid id, it returns the auction")
    public void getAuction() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header("Content-Type", "application/json")
                .header(COOKIE, adminAuthToken)
        .when()
                .get("1")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.id", equalTo(1),
                        "data.product", equalTo("TEST"),
                        "data.minPrice", equalTo(1.0f),
                        "data.quantity", equalTo(10),
                        "data.totalRevenue", equalTo(0),
                        "data.totalQuantitySold", equalTo(0),
                        "data.bids.size()", equalTo(1),
                        "data.winningBids.size()", equalTo(0),
                        "data.losingBids.size()", equalTo(0));
        //@formatter:on
    }

    @Test
    @DisplayName("when GET /auctions is provided an invalid id, it returns 404 and a message")
    public void getAuctionInvalidId() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header("Content-Type", "application/json")
                .header(COOKIE, adminAuthToken)
        .when()
                .get("-1")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
        //@formatter:on
    }

    @Test
    @DisplayName("when GET /auctions returns a list of auctions excluding the requester's")
    public void getAllAuctions() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header("Content-Type", "application/json")
                .header(COOKIE, adminAuthToken)
        .when()
                .get()
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.size()", equalTo(2));
        //@formatter:on
    }

    @Test
    @DisplayName("when GET /auctions/available returns a list of open auctions belonging to the requester")
    public void getAvailableAuctions() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header("Content-Type", "application/json")
                .header(COOKIE, adminAuthToken)
        .when()
                .get("available")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.size()", equalTo(1));
        //@formatter:on
    }

    @Test
    @DisplayName("when PUT /auctions/{id}/bids make a bid on the auction matching the id")
    public void putAuctionMakeABid() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header("Content-Type", "application/json")
                .header(COOKIE, adminAuthToken)
        .when()
                .body(generateJSON("offerPrice", "3.2", "quantity", "10"))
                .put("1/bids")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.offerPrice", equalTo(3.2f),
                        "data.quantity", equalTo(10),
                        "data.bidderId", equalTo(3),
                        "data.id", equalTo(1));
        //@formatter:on
    }

    @Test
    @DisplayName("when PUT /{id}/close close the auction matching the id")
    public void putCloseAuction() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header("Content-Type", "application/json")
                .header(COOKIE, apiData.user1Token())
        .when()
                .put("1/close")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.id", equalTo(1),
                        "data.status", equalTo(AuctionStatus.CLOSED.name()));
        //@formatter:on
    }

    @Test
    @DisplayName("when PUT /{id}/close and auction doesn't exist returns 404 and a message")
    public void putCloseAuctionInvalidAuction() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header("Content-Type", "application/json")
                .header(COOKIE, adminAuthToken)
        .when()
                .put("-2/close")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
        //@formatter:on
    }

    @Test
    @DisplayName("when PUT /{id}/close and userRole doesn't own resource returns 403 and a message")
    public void putCloseAuctionUserDoesntOwnAuction() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header("Content-Type", "application/json")
                .header(COOKIE, adminAuthToken)
        .when()
                .put("1/close")
        .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
        //@formatter:on
    }
}
