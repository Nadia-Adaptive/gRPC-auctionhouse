package com.weareadaptive.auctionhouse.console;

import com.weareadaptive.auctionhouse.model.ModelState;
import com.weareadaptive.auctionhouse.model.TimeContext;
import com.weareadaptive.auctionhouse.model.User;

import java.io.PrintStream;
import java.util.Scanner;

public class MenuContext {
    private final ModelState state;
    private final Scanner scanner;
    private final PrintStream out;
    private User currentUser;

    private final TimeContext timeContext;

    public MenuContext(final ModelState state, final Scanner scanner, final PrintStream out,
                       final TimeContext timeContext) {
        this.state = state;
        this.scanner = scanner;
        this.out = out;
        this.timeContext = timeContext;
    }

    public ModelState getState() {
        return state;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public PrintStream getOut() {
        return out;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(final User currentUser) {
        this.currentUser = currentUser;
    }

    public TimeContext getTimeContext() {
        return timeContext;
    }
}
