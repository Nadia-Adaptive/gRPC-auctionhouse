package com.weareadaptive.auctionhouse.console.admin;

import com.weareadaptive.auctionhouse.console.MenuContext;
import com.weareadaptive.auctionhouse.model.AccessStatus;
import com.weareadaptive.auctionhouse.model.AuctionState;
import com.weareadaptive.auctionhouse.model.InstantTimeProvider;
import com.weareadaptive.auctionhouse.model.ModelState;
import com.weareadaptive.auctionhouse.model.OrganisationState;
import com.weareadaptive.auctionhouse.model.TimeContext;
import com.weareadaptive.auctionhouse.model.UserState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Scanner;
import java.util.stream.Stream;

import static com.weareadaptive.auctionhouse.TestData.USER1;
import static com.weareadaptive.auctionhouse.TestData.USER2;
import static com.weareadaptive.auctionhouse.TestData.USER3;
import static com.weareadaptive.auctionhouse.TestData.USER4;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserManagementMenuTest {
    private static Stream<Arguments> testArguments() {
        return Stream.of(
                Arguments.of("1\n%s\n\r\n7".formatted("q")),
                Arguments.of("1\n%s\n%s\n\r\n7".formatted(
                        USER1.getUsername(),
                        "q")),
                Arguments.of("1\n%s\n%s\n%s\n%s\n\r\n7".formatted(
                        USER1.getUsername(),
                        "password",
                        "password",
                        "q")),
                Arguments.of("1\n%s\n%s\n%s\n%s\n%s\n\r\n7".formatted(
                        USER1.getUsername(),
                        "password",
                        "password",
                        USER1.getFirstName(),
                        "q")),
                Arguments.of("1\n%s\n%s\n%s\n%s\n%s\n%s\n\r\n7".formatted(
                        USER1.getUsername(),
                        "password",
                        "password",
                        USER1.getFirstName(),
                        USER1.getLastName(),
                        "q"))
        );
    }

    private MenuContext createUserContext(final String src) {
        final UserManagementMenu menu = new UserManagementMenu();
        Scanner scanner = new Scanner(src);
        MenuContext context =
                new MenuContext(new ModelState(new UserState(), new OrganisationState(), new AuctionState()), scanner,
                        System.out, new TimeContext(new InstantTimeProvider()));
        context.getState().userState().add(USER2);
        context.getState().userState().add(USER3);
        context.getState().userState().add(USER4);

        context.getState().organisationState().addUserToOrganisation(USER2);
        context.getState().organisationState().addUserToOrganisation(USER3);
        context.getState().organisationState().addUserToOrganisation(USER4);

        menu.display(context);

        return context;
    }

    @Test
    @DisplayName("Create User option should add a new user with valid parameters to the app's state")
    public void createUserShouldAddANewUserToUserState() {
        final var password = "PASSWORD";
        final MenuContext context = createUserContext("%d\n%s\n%s\n%s\n%s\n%s\n%s\n\r\n7".formatted(
                1,
                USER1.getUsername(),
                password,
                password,
                USER1.getFirstName(),
                USER1.getLastName(),
                USER1.getOrganisation()
        ));

        assertTrue(context.getState().userState().containsUser(USER1.getUsername()));
    }

    @Test
    @DisplayName("Update User details operation should change an existing user's details with valid parameters")
    public void updateUserShouldModifyExistingUser() {
        final var password = "PASSWORD";
        final MenuContext context = createUserContext("%d\n%s\n%s\n%s\n%s\n%s\n%s\n\r\n7".formatted(
                1,
                USER1.getUsername(),
                password,
                password,
                USER1.getFirstName(),
                USER1.getLastName(),
                USER1.getOrganisation()
        ));

        assertTrue(context.getState().userState().containsUser(USER1.getUsername()));
    }

    @ParameterizedTest
    @DisplayName("Update User details operation should terminate if user enters Q at any stage")
    @MethodSource("testArguments")
    public void createUserShouldTerminatesOnUserRequest(final String src) {
        assertDoesNotThrow(() -> createUserContext(src));
    }

    @Test
    @DisplayName("admin can change users access status")
    public void adminCanChangeUserStatus() {
        assertDoesNotThrow(() -> createUserContext("4\n%s\nblock\n7".formatted(USER2.getUsername())));

        assertEquals(AccessStatus.BLOCKED, USER2.getAccessStatus());
    }

    @Test
    @DisplayName("admin can view users information")
    public void adminCanViewUserInformation() {
        assertDoesNotThrow(() -> createUserContext("2\n\r7"));
    }

    @Test
    @DisplayName("admin can get view all organisations")
    public void adminCanViewAllOrganisations() {
        assertDoesNotThrow(() -> createUserContext("5\n\r7"));
    }

    @Test
    @DisplayName("admin can get view all organisations details")
    public void adminCanViewOrganisationDetails() {
        assertDoesNotThrow(() -> createUserContext("6\n\r7"));
    }
}
