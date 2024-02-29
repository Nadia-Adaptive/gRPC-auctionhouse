package com.weareadaptive.auctionhouse.console.admin;

import com.weareadaptive.auctionhouse.console.ConsoleMenu;
import com.weareadaptive.auctionhouse.console.MenuContext;
import com.weareadaptive.auctionhouse.model.AccessStatus;
import com.weareadaptive.auctionhouse.model.User;
import com.weareadaptive.auctionhouse.utils.StringUtil;

import java.util.Optional;

import static com.weareadaptive.auctionhouse.utils.PromptUtil.CANCEL_OPERATION_TEXT;
import static com.weareadaptive.auctionhouse.utils.PromptUtil.INVALID_INPUT_MESSAGE;
import static com.weareadaptive.auctionhouse.utils.PromptUtil.LEAVE_FIELD_BLANK_TEXT;
import static com.weareadaptive.auctionhouse.utils.PromptUtil.TERMINATED_OPERATION_TEXT;
import static com.weareadaptive.auctionhouse.utils.PromptUtil.getStringInput;
import static com.weareadaptive.auctionhouse.utils.PromptUtil.getStringOrEmptyInput;
import static com.weareadaptive.auctionhouse.utils.PromptUtil.hasUserTerminatedOperation;
import static com.weareadaptive.auctionhouse.utils.StringUtil.isNullOrEmpty;
import static com.weareadaptive.auctionhouse.utils.StringUtil.organisationToString;

public class UserManagementMenu extends ConsoleMenu {
    @Override
    public void display(final MenuContext context) {
        createMenu(context,
                option("Create user", this::createNewUser),
                option("Show users", this::listAllUsers),
                option("Update user details", this::updateExistingUser),
                option("Block/Unblock a user", this::changeUserAccess),
                option("Show organisations", this::listOrganisations),
                option("Show organisations details", this::listOrganisationsDetails),
                leave("Go back")
        );
    }

    private void changeUserAccess(final MenuContext context) {
        var out = context.getOut();
        printAllUsers(context);
        out.println(CANCEL_OPERATION_TEXT);

        var user = getUser(context);
        if (user.isEmpty()) {
            out.println(TERMINATED_OPERATION_TEXT);
            return;
        }

        do {
            var input = getStringInput(context, "Please input allow or block to change the user's access.");
            if (hasUserTerminatedOperation(input)) {
                out.println(TERMINATED_OPERATION_TEXT);
                return;
            }

            switch (input.toLowerCase()) {
                case "block" -> user.get().setAccessStatus(AccessStatus.BLOCKED);
                case "allow" -> user.get().setAccessStatus(AccessStatus.ALLOWED);
                default -> {
                    out.println(INVALID_INPUT_MESSAGE);
                    continue;
                }
            }
            out.println("User's access updated.");
            break;
        } while (true);
    }

    private void updateExistingUser(final MenuContext context) {
        final var out = context.getOut();
        printAllUsers(context);
        out.println(CANCEL_OPERATION_TEXT);

        final var user = getUser(context);
        if (user.isEmpty()) {
            out.println(TERMINATED_OPERATION_TEXT);
            return;
        }

        final String uName = getUsername(context, "What is the new username?\n%s".formatted(LEAVE_FIELD_BLANK_TEXT));
        final String fName = getStringOrEmptyInput(context, "What is the new first name?");
        final String password = getPassword(context, "What is the new password?\n%s".formatted(LEAVE_FIELD_BLANK_TEXT));
        final String lName = getStringOrEmptyInput(context, "What is new last name?");
        final String organisation = getStringOrEmptyInput(context, "What is the user's new organisation?");
        final String oldOrganisation = user.get().getOrganisation();


        user.get().update(hasUserTerminatedOperation(uName) ? "" : uName, password, fName, lName, organisation);

        if (!isNullOrEmpty(organisation) && user.get().getOrganisation() != oldOrganisation) {
            context.getState().organisationState().removeUserFromOrganisation(user.get(), oldOrganisation);
        }

        out.println("User updated.");
        pressEnter(context);
    }

    private Optional<User> getUser(final MenuContext context) {
        final var state = context.getState().userState();
        final var out = context.getOut();
        do {
            final var uName = getStringInput(context, "Please choose a user by entering their username.");

            if (hasUserTerminatedOperation(uName)) {
                return Optional.empty();
            }

            if (!state.containsUser(uName)) {
                out.println("User does not exist. Please try again or input Q to quit.");
                continue;
            }
            return state.stream().filter(u -> u.getUsername().equals(uName)).findFirst();
        } while (true);
    }

    private void createNewUser(final MenuContext context) {
        var userState = context.getState().userState();
        var orgState = context.getState().organisationState();
        var out = context.getOut();

        out.println(CANCEL_OPERATION_TEXT);

        String uName = getUsername(context, "What is the user's username");
        if (hasUserTerminatedOperation(uName)) {
            out.println(TERMINATED_OPERATION_TEXT);
            return;
        }

        String password = getPassword(context, "Enter the user's password");
        if (hasUserTerminatedOperation(password)) {
            out.println(TERMINATED_OPERATION_TEXT);
            return;
        }

        String fName = getStringInput(context, "What is the user's first name?");
        if (hasUserTerminatedOperation(fName)) {
            out.println(TERMINATED_OPERATION_TEXT);
            return;
        }

        String lName = getStringInput(context, "What is the user's last name?");
        if (hasUserTerminatedOperation(lName)) {
            out.println(TERMINATED_OPERATION_TEXT);
            return;
        }

        final String organisation = getStringInput(context, "Where does the user work?");
        if (hasUserTerminatedOperation(organisation)) {
            out.println(TERMINATED_OPERATION_TEXT);
            return;
        }

        final var newUser = new User(userState.nextId(), uName, password, fName, lName, organisation);

        out.printf("Created new user %s%n", newUser.getUsername());
        orgState.addUserToOrganisation(newUser);
        userState.add(newUser);

        pressEnter(context);
    }

    private String getPassword(final MenuContext context, final String prompt) {
        final var out = context.getOut();
        final var scanner = context.getScanner();

        do {
            out.println(prompt);
            final var password = readPassword(scanner);

            if (password.equalsIgnoreCase("q")) {
                return password;
            }

            out.println("Confirm the user's password:");
            if (password.equals(readPassword(scanner))) {
                return password;
            } else {
                out.println("The passwords don't match. Please try again.");
            }
        } while (true);
    }

    private String getUsername(final MenuContext context, final String prompt) {
        var out = context.getOut();

        do {
            String username = getStringInput(context, prompt);

            if (username.matches("^[a-zA-Z0-9]*$")) {
                return username;
            }
            out.println("Invalid username. Please try again.");
        } while (true);
    }

    private void listOrganisationsDetails(final MenuContext context) {
        var out = context.getOut();
        var orgState = context.getState().organisationState();

        out.println("== Organisation details");
        orgState.getAllDetails()
                .map((o) -> organisationToString(o))
                .sorted()
                .forEach(out::println);

        pressEnter(context);
    }

    private void listOrganisations(final MenuContext context) {
        var out = context.getOut();
        var orgState = context.getState().organisationState();

        out.println("== All organisations");
        orgState.getAllOrganisations().sorted().forEach(out::println);

        pressEnter(context);
    }

    private void listAllUsers(final MenuContext context) {
        var out = context.getOut();

        out.println("== All Users");
        printAllUsers(context);

        pressEnter(context);
    }

    private void printAllUsers(final MenuContext context) {
        final var userState = context.getState().userState();
        userState.stream().forEach(u -> context.getOut().println(StringUtil.userToString(u)));
    }
}
