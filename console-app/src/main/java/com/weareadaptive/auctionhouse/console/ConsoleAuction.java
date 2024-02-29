package com.weareadaptive.auctionhouse.console;

import com.weareadaptive.auctionhouse.model.AuctionState;
import com.weareadaptive.auctionhouse.model.InstantTimeProvider;
import com.weareadaptive.auctionhouse.model.ModelState;
import com.weareadaptive.auctionhouse.model.OrganisationState;
import com.weareadaptive.auctionhouse.model.TimeContext;
import com.weareadaptive.auctionhouse.model.User;
import com.weareadaptive.auctionhouse.model.UserState;

import java.util.Scanner;
import java.util.stream.Stream;


public class ConsoleAuction {
    private final MenuContext menuContext;

    public ConsoleAuction() {
        var state = new ModelState(new UserState(), new OrganisationState(), new AuctionState());
        initData(state);
        var scanner = new Scanner(System.in);
        menuContext = new MenuContext(state, scanner, System.out, new TimeContext(new InstantTimeProvider()));
    }

    // Creates the initial set of users
    private void initData(final ModelState state) {
        Stream.of(
                        new User(state.userState().nextId(), "admin", "admin", "admin", "admin", "admin", true),
                        new User(state.userState().nextId(), "jf", "mypassword", "JF", "Legault", "Org 1"),
                        new User(state.userState().nextId(), "thedude", "biglebowski", "Walter", "Sobchak", "Org 2")
                )
                .forEach(u -> {
                    state.userState().add(u);
                    state.organisationState().addUserToOrganisation(u);
                });
//        state.auctionState().add(new Auction(state.auctionState().nextId(), "admin", "jypUSD", 0.1, 10));
//        state.auctionState().get(0).makeBid(new Bid("jf", 0.4, 5));
    }

    public void start() {
        LoginMenu loginMenu = new LoginMenu();
        loginMenu.display(menuContext);
    }

}
