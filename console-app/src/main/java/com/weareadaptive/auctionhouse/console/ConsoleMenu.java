package com.weareadaptive.auctionhouse.console;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ConsoleMenu {

    public abstract void display(MenuContext context);

    protected MenuOption option(final String title, final Consumer<MenuContext> action) {
        return new MenuOption(title, action, c -> true);
    }

    protected MenuOption option(final String title, final Runnable action) {
        return new MenuOption(title, c -> action.run(), c -> true);
    }

    protected MenuOption option(final String title, final Consumer<MenuContext> action,
                                final Predicate<MenuContext> isShown) {
        return new MenuOption(title, action, isShown);
    }

    protected MenuOption option(final String title, final Runnable action, final Predicate<MenuContext> isDisplayed) {
        return new MenuOption(title, c -> action.run(), isDisplayed);
    }

    protected MenuOption leave(final String title) {
        return new MenuOption(title, c -> {
        }, c -> true, true);
    }

    protected void createMenu(final MenuContext context, final MenuOption... options) {
        var scanner = context.getScanner();
        var out = context.getOut();

        var success = false;
        while (!success) {
            out.println("===================================");
            for (var i = 1; i <= options.length; i++) {
                var option = options[i - 1];
                if (option.isDisplayed.test(context)) {
                    out.printf("%s. %s %n", i, option.title);
                }
            }
            out.println("===================================");
            out.println("Enter your option: ");
            var invalidOption = false;
            try {
                var rawOption = scanner.nextLine();
                var option = Integer.parseInt(rawOption);
                if (option > 0 && option <= options.length && options[option - 1].isDisplayed.test(context)) {
                    options[option - 1].action.accept(context);
                    success = options[option - 1].leave;
                } else {
                    invalidOption = true;
                }
            } catch (final InputMismatchException | NumberFormatException mismatchException) {
                invalidOption = true;
            }
            if (invalidOption) {
                out.println("Invalid option");
            }
        }
    }

    protected void pressEnter(final MenuContext context) {
        context.getOut().println("Press Enter key to continue...");
        context.getScanner().nextLine();
    }

    protected String readPassword(final Scanner scanner) {
        if (System.console() != null) {
            return new String(System.console().readPassword());
        } else {
            // There is no Console in IntelliJ
            return scanner.nextLine();
        }
    }

    public static final class MenuOption {
        final String title;
        final Consumer<MenuContext> action;
        final boolean leave;
        private final Predicate<MenuContext> isDisplayed;

        private MenuOption(final String title, final Consumer<MenuContext> action,
                           final Predicate<MenuContext> isDisplayed) {
            this(title, action, isDisplayed, false);
        }

        private MenuOption(final String title, final Consumer<MenuContext> action,
                           final Predicate<MenuContext> isDisplayed, final boolean leave) {
            this.isDisplayed = isDisplayed;
            this.leave = leave;
            this.title = title;
            this.action = action;
        }
    }
}
