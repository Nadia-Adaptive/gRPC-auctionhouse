package com.weareadaptive.auction.user;

import com.weareadaptive.auction.response.ResponseStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;
import java.util.stream.Stream;

import static com.weareadaptive.auction.TestData.ADMIN_ORG;
import static com.weareadaptive.auction.TestData.ORG_1;
import static com.weareadaptive.auction.TestData.ORG_2;
import static com.weareadaptive.auction.TestData.generateJSON;
import static com.weareadaptive.auction.apiTestData.adminAuthToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.HttpHeaders.COOKIE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
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
        uri = "http://localhost:" + port + "/api/v1/users/";
    }

    private static final String username = "test";
    private static final String password = "testPassword";

    private static Map<String, String> postUserJSON(final String firstName, final String lastName,
                                                    final String password, final String organisationName) {
        return generateJSON("password", username, "firstName", firstName, "lastName", lastName,
                "password", password, "organisationName", organisationName);
    }

    private static Map<String, String> postUserJSON(final String firstName, final String lastName,
                                                    final String password, final String organisationName,
                                                    final UserRole userRole) {
        final var input = postUserJSON(firstName, lastName, password, organisationName);
        input.put("userRole", userRole.name());
        input.put("username", "userRole" + userRole.name());
        return input;
    }


    private static Stream<Map<String, String>> invalidInput() {
        return Stream.of(
                postUserJSON("", username, password, ORG_1, UserRole.USER),
                postUserJSON(username, "", password, ORG_1, UserRole.USER),
                postUserJSON(username, username, "", ORG_1, UserRole.USER),
                postUserJSON(username, username, password, "", UserRole.USER),
                postUserJSON(username, username, password, ORG_1),
                postUserJSON(username, username, password, ORG_1, UserRole.ADMIN),
                postUserJSON(username, username, password, "ADMIN", UserRole.USER));
    }

    @Test
    @DisplayName("GetUser_UserExistsAndRoleIsAdmin_ReturnsUserAnd200")
    public void getUser() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header(COOKIE, adminAuthToken)
        .when()
                .get("1")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body(
                        "data.id", equalTo(1),
                        "data.username", equalTo("ADMIN"),
                        "data.firstName", equalTo("admin"),
                        "data.lastName", equalTo("admin"),
                        "data.organisationName", equalTo(ADMIN_ORG),
                        "data.accessStatus", equalTo("ALLOWED"),
                        "data.userRole", equalTo("ADMIN"));
        //@formatter:on
    }

    @Test
    @DisplayName("GetUser_UserDoesntExistAndRoleIsAdmin_ReturnsMessageAnd404")
    public void getNonExistentUser() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header(COOKIE, adminAuthToken)
        .when().get("-1")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
        //@formatter:on
    }

    @Test
    @DisplayName("PostUser_CreateUserWithValidInputs_ReturnsMessageAnd201")
    public void postUser() {
        final var userInput = postUserJSON(username, username, password, ORG_1, UserRole.USER);
        //@formatter:off
        given()
                .baseUri(uri)
                .header(COOKIE, adminAuthToken)
                .header("Content-Type", "application/json")
        .when()
                .body(userInput)
                .post()
        .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("data.id", equalTo(4),
                        "data.username", equalTo(username),
                        "data.firstName", equalTo(userInput.get(username)),
                        "data.lastName", equalTo(userInput.get(username)),
                        "data.organisationName", equalTo(ORG_1),
                        "data.userRole", equalTo(UserRole.USER));
        //@formatter:on
    }

    @Test
    @DisplayName("PostUser_CreateAdminWithValidInputs_ReturnsMessageAnd201")
    public void postAdmin() {
        final var userInput = postUserJSON(username, username, password, ADMIN_ORG, UserRole.ADMIN);
        //@formatter:off
        given()
                .baseUri(uri)
                .header(COOKIE, adminAuthToken)
        .when()
                .header("Content-Type", "application/json")
                .body(userInput)
                .post()
        .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("data.id", equalTo(5),
                        "data.username", equalTo(username),
                        "data.firstName", equalTo(userInput.get(username)),
                        "data.lastName", equalTo(userInput.get(username)),
                        "data.organisationName", equalTo(ADMIN_ORG),
                        "data.userRole", equalTo(UserRole.ADMIN));
        //@formatter:off
    }

    @ParameterizedTest()
    @DisplayName("PostUser_createUserWithInvalidInputs_ReturnsMessageAnd400")
    @MethodSource("invalidInput")
    public void postUserHandlesInvalidInputs(final Map<String, String> input) {
        //@formatter:off
        given()
                .baseUri(uri)
                .header(COOKIE, adminAuthToken)
        .when()
                .header("Content-Type", "application/json")
                .body(input)
                .post()
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message",
                        equalTo(ResponseStatus.BAD_REQUEST));
        //@formatter:on
    }

    @Test()
    @DisplayName("PutUser_UpdateUserWithValidInputs_ReturnsMessageAnd200")
    public void putUser() {
        final var input = generateJSON("firstName", username, "lastName", "last", "organisationName", ORG_2);
        //@formatter:off
        given()
                .baseUri(uri)
                .header(COOKIE, adminAuthToken)
        .when()
                .header("Content-Type", "application/json")
                .body(input)
                .put("2")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("message", equalTo(ResponseStatus.OK));
        //@formatter:on
    }

    @Test()
    @DisplayName("PutUser_AttemptUpdateNonExistentUser_ReturnsMessageAnd404")
    public void putNonExistentUser() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header(COOKIE, adminAuthToken)
        .when()
                .header("Content-Type", "application/json")
                .body(generateJSON("firstName", "TEST01"))
                .put("-1")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
        //@formatter:on
    }

    @Test()
    @DisplayName("PutUserStatus_UpdateUserWithValidAccessStatus_ReturnsMessageAnd200")
    public void putUserStatus() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header(COOKIE, adminAuthToken)
        .when()
                .header("Content-Type", "application/json")
                .body(generateJSON("accessStatus", "BLOCKED"))
                .put("2/status")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.status", equalTo(AccessStatus.BLOCKED.getStatus()));
        //@formatter:on
    }

    @Test()
    @DisplayName("PutUserStatus_UpdateUserWithValidAccessStatus_ReturnsMessageAnd400")
    public void putUserStatusWithInvalidAccess() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header(COOKIE, adminAuthToken)
        .when()
                .header("Content-Type", "application/json")
                .body(generateJSON("accessStatus", "BLOCK"))
                .put("2/status")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        //@formatter:on
    }

    @Test()
    @DisplayName("GET users/2/auctions returns 1 auction")
    public void getUserAuctions() {
        //@formatter:off
        given()
                .baseUri(uri)
                .header(COOKIE, adminAuthToken)
        .when()
                .header("Content-Type", "application/json")
                .get("2/auctions")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.size()", equalTo(1));
        //@formatter:on
    }
}