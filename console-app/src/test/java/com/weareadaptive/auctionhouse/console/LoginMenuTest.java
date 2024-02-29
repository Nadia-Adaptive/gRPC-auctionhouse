package com.weareadaptive.auctionhouse.console;

import com.weareadaptive.auctionhouse.model.AccessStatus;
import com.weareadaptive.auctionhouse.model.AuctionState;
import com.weareadaptive.auctionhouse.model.InstantTimeProvider;
import com.weareadaptive.auctionhouse.model.ModelState;
import com.weareadaptive.auctionhouse.model.OrganisationState;
import com.weareadaptive.auctionhouse.model.TimeContext;
import com.weareadaptive.auctionhouse.model.UserState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static com.weareadaptive.auctionhouse.TestData.ADMIN;
import static com.weareadaptive.auctionhouse.TestData.USER2;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class LoginMenuTest {
    private static MenuContext createContext(final String src) {
        System.out.println(src);
        final LoginMenu menu = new LoginMenu();
        Scanner scanner = new Scanner(src);
        MenuContext context = new MenuContext(new ModelState(new UserState(), new OrganisationState(),
                new AuctionState()), scanner, System.out, new TimeContext(new InstantTimeProvider()));

        context.getState().userState().add(USER2);
        context.getState().userState().add(ADMIN);

        menu.display(context);

        return context;
    }

    @Test
    @DisplayName("users with a blocked access status cannot login")
    void restrictUserAccess() {
        USER2.setAccessStatus(AccessStatus.BLOCKED);
        assertDoesNotThrow(() -> createContext("1\n%s\n%s\n2".formatted(USER2.getUsername(), "password")));
    }

    @Test
    @DisplayName("admins can access the user management menu")
    void adminCanAccessUserManagement() {
        assertDoesNotThrow(() -> createContext("1\n%s\n%s\n1\n7\n3\n2".formatted(ADMIN.getUsername(), "admin")));
    }

    @Test
    @DisplayName("users can't access the user management menu")
    void userCannotAccessUserManagement() {
        assertDoesNotThrow(() -> createContext("1\n%s\n%s\n1\n3\n2".formatted(USER2.getUsername(), "password")));
    }

    @Test
    @DisplayName("users can access the auction management menu")
    void userCanAccessAuctionManagement() {
        assertDoesNotThrow(() -> createContext("1\n%s\n%s\n2\n7\n3\n2".formatted(USER2.getUsername(), "password")));
    }

    @Test
    @DisplayName("admins can access the auction management menu")
    void adminCanAccessAuctionManagement() {
        assertDoesNotThrow(() -> createContext("1\n%s\n%s\n2\n7\n3\n2".formatted(ADMIN.getUsername(), "admin")));
    }
}
