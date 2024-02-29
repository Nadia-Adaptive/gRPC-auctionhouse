package com.weareadaptive.auctionhouse.console;

import com.weareadaptive.auctionhouse.console.admin.UserManagementMenu;
import com.weareadaptive.auctionhouse.console.auction.AuctionManagementMenu;
import com.weareadaptive.auctionhouse.model.AccessStatus;

public class LoginMenu extends ConsoleMenu {

    private final UserManagementMenu userMenu;
    private final AuctionManagementMenu auctionMenu;

    public LoginMenu() {
        userMenu = new UserManagementMenu();
        auctionMenu = new AuctionManagementMenu();
    }

    @Override
    public void display(final MenuContext context) {
        createMenu(
                context,
                option("Login", this::login),
                leave("Quit")
        );
    }

    private void login(final MenuContext context) {
        var out = context.getOut();

        out.println("Enter your username:");
        out.flush();
        var username = context.getScanner().nextLine();

        out.println("Enter your password:");
        var password = readPassword(context.getScanner());

        context.getState()
                .userState()
                .findUserByUsername(username, password)
                .ifPresentOrElse(user -> {
                    context.setCurrentUser(user);
                    if (user.getAccessStatus() == AccessStatus.ALLOWED) {
                        out.printf("Welcome %s %s %n", user.getFirstName(), user.getLastName());
                        createManagementMenus(context);
                    } else {
                        out.println("You are not authorized. Please contact support.");
                    }
                }, () -> out.println("Invalid username/password combination"));
    }

    private void createManagementMenus(final MenuContext context) {
        createMenu(
                context,
                option("User management", this::userManagementMenu, (c) -> c.getCurrentUser().isAdmin()),
                option("Auction management", this::auctionManagementMenu),
                leave("Log out")
        );
    }

    private void userManagementMenu(final MenuContext context) {
        userMenu.display(context);
    }

    private void auctionManagementMenu(final MenuContext context) {
        auctionMenu.display(context);
    }
}
