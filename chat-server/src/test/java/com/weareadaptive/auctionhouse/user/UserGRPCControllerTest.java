package com.weareadaptive.auctionhouse.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

import static com.weareadaptive.auctionhouse.TestData.ADMIN_ORG;
import static com.weareadaptive.auctionhouse.TestData.ORG_1;
import static com.weareadaptive.auctionhouse.TestData.generateJSON;
import static org.junit.Assert.fail;

class UserGRPCControllerTest {
    private static final String USERNAME = "test";
    private static final String PASSWORD = "testPassword";

    private static Map<String, String> postUserJSON(final String firstName, final String lastName,
                                                    final String password, final String organisationName) {
        return generateJSON("password", USERNAME, "firstName", firstName, "lastName", lastName,
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
                postUserJSON("", USERNAME, PASSWORD, ORG_1, UserRole.USER),
                postUserJSON(USERNAME, "", PASSWORD, ORG_1, UserRole.USER),
                postUserJSON(USERNAME, USERNAME, "", ORG_1, UserRole.USER),
                postUserJSON(USERNAME, USERNAME, PASSWORD, "", UserRole.USER),
                postUserJSON(USERNAME, USERNAME, PASSWORD, ORG_1),
                postUserJSON(USERNAME, USERNAME, PASSWORD, ORG_1, UserRole.ADMIN),
                postUserJSON(USERNAME, USERNAME, PASSWORD, "ADMIN", UserRole.USER));
    }

    @Test
    @DisplayName("GetUser_UserExistsAndRoleIsAdmin_ReturnsUserAnd200")
    public void getUser() {
        fail();
    }

    @Test
    @DisplayName("GetUser_UserDoesntExistAndRoleIsAdmin_ReturnsMessageAnd404")
    public void getNonExistentUser() {
        fail();
    }

    @Test
    @DisplayName("PostUser_CreateUserWithValidInputs_ReturnsMessageAnd201")
    public void postUser() {
        final var userInput = postUserJSON(USERNAME, USERNAME, PASSWORD, ORG_1, UserRole.USER);
        fail();
    }

    @Test
    @DisplayName("PostUser_CreateAdminWithValidInputs_ReturnsMessageAnd201")
    public void postAdmin() {
        final var userInput = postUserJSON(USERNAME, USERNAME, PASSWORD, ADMIN_ORG, UserRole.ADMIN);
        fail();
    }

    @DisplayName("PostUser_createUserWithInvalidInputs_ReturnsMessageAnd400")
    public void postUserHandlesInvalidInputs(final Map<String, String> input) {
        fail();
    }

    @Test()
    @DisplayName("PutUser_UpdateUserWithValidInputs_ReturnsMessageAnd200")
    public void putUser() {
        fail();
    }

    @Test()
    @DisplayName("PutUser_AttemptUpdateNonExistentUser_ReturnsMessageAnd404")
    public void putNonExistentUser() {
        fail();
    }

    @Test()
    @DisplayName("PutUserStatus_UpdateUserWithValidAccessStatus_ReturnsMessageAnd200")
    public void putUserStatus() {
        fail();
    }

    @Test()
    @DisplayName("PutUserStatus_UpdateUserWithValidAccessStatus_ReturnsMessageAnd400")
    public void putUserStatusWithInvalidAccess() {
        fail();
    }

    @Test()
    @DisplayName("GET users/2/auctions returns 1 auction")
    public void getUserAuctions() {
        fail();
    }
}
