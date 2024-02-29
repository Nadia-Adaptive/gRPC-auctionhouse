package com.weareadaptive.auctionhouse.console.auction;

import com.weareadaptive.auctionhouse.console.MenuContext;
import com.weareadaptive.auctionhouse.model.Auction;
import com.weareadaptive.auctionhouse.model.AuctionState;
import com.weareadaptive.auctionhouse.model.AuctionStatus;
import com.weareadaptive.auctionhouse.model.Bid;
import com.weareadaptive.auctionhouse.model.InstantTimeProvider;
import com.weareadaptive.auctionhouse.model.ModelState;
import com.weareadaptive.auctionhouse.model.OrganisationState;
import com.weareadaptive.auctionhouse.model.TimeContext;
import com.weareadaptive.auctionhouse.model.UserState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.Scanner;

import static com.weareadaptive.auctionhouse.TestData.USER1;
import static com.weareadaptive.auctionhouse.TestData.USER2;
import static com.weareadaptive.auctionhouse.TestData.USER3;
import static com.weareadaptive.auctionhouse.TestData.USER4;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuctionManagementMenuTest {
    private static MenuContext createContext(final String src, final Optional<AuctionState> auctionState) {
        final AuctionManagementMenu menu = new AuctionManagementMenu();
        Scanner scanner = new Scanner(src);
        MenuContext context = new MenuContext(new ModelState(new UserState(), new OrganisationState(),
                auctionState.orElse(new AuctionState())), scanner, System.out, new TimeContext(new InstantTimeProvider()));
        context.setCurrentUser(USER1);

        menu.display(context);

        return context;
    }

    public static MenuContext createTwoOpenAuctions(final String src) {
        final var auctionState = new AuctionState();
        auctionState.add(new Auction(auctionState.nextId(), USER2, "MSFT", 1.03d, 100));
        auctionState.add(new Auction(auctionState.nextId(), USER2, "APPL", 1.05d, 100));

        auctionState.get(0).placeABid(new Bid(USER1, 1.2d, 80, Instant.ofEpochSecond(1000)));
        auctionState.get(0).placeABid(new Bid(USER1, 1.2d, 40, Instant.ofEpochSecond(900)));
        auctionState.get(0).placeABid(new Bid(USER1, 1.1d, 10, Instant.ofEpochSecond(100)));

        auctionState.get(0).close();

        return createContext(src, Optional.of((auctionState)));
    }

    public static MenuContext createTwoUserAuctions(final String src) {
        final var auctionState = new AuctionState();
        auctionState.add(new Auction(auctionState.nextId(), USER1, "JAGU", 1.02d, 100));
        auctionState.add(new Auction(auctionState.nextId(), USER1, "FORD", 1.02d, 100));

        auctionState.get(0).placeABid(new Bid(USER3, 1.2d, 40, Instant.ofEpochSecond(100)));
        auctionState.get(0).placeABid(new Bid(USER4, 1.1d, 10, Instant.ofEpochSecond(100)));

        return createContext(src, Optional.of((auctionState)));
    }

    @Test
    @DisplayName("auctionManagementMenu can create an auction through the menu")
    public void canCreateAuction() {
        final var context = createContext("1\nEURUSD\n1.55\n1000\n\r7", Optional.empty());

        final var auctionState = context.getState().auctionState();
        final var auction = auctionState.get(0);

        assertEquals(1, auctionState.stream().count());
        assertEquals("EURUSD", auction.getSymbol());
        assertEquals(1.55d, auction.getMinPrice());
        assertEquals(1000, auction.getQuantity());
    }

    @Test
    @DisplayName("auctionManagementMenu can place a bid through the menu")
    public void canPlaceABid() {
        final var context = createTwoOpenAuctions("4\n1\n10\n5\n\r7");

        final var auction = context.getState().auctionState().get(1);

        assertEquals(1, auction.getBids().count());
    }

    @Test
    @DisplayName("auctionManagementMenu cant place a bid on user's own auction")
    public void cantPlaceABid() {
        final var context = createTwoUserAuctions("4\n2\nq\n7");

        final var auction = context.getState().auctionState().get(0);

        assertEquals(2, auction.getBids().count());
    }

    @Test
    @DisplayName("auctionManagementMenu closes the current user's auction through the menu")
    public void canCloseAuction() {
        final var context = createTwoUserAuctions("3\n0\nyes\n\r7");

        final var auction = context.getState().auctionState().get(0);

        assertEquals(AuctionStatus.CLOSED, auction.getStatus());
    }

    @Test
    @DisplayName("auctionManagementMenu does not close an auction if the current user does not own it")
    public void cantCloseAuction() {
        final var context = createTwoOpenAuctions("3\n0\nq\n\r7");

        final var auction = context.getState().auctionState().get(1);

        assertEquals(AuctionStatus.OPEN, auction.getStatus());
    }

    @Test
    @DisplayName("auctionManagementMenu displays all a user's auctions")
    public void displayUserAuctions() {
        assertDoesNotThrow(() -> createTwoUserAuctions("3\n0\nyes\n2\n\r7"));
    }

    @Test
    @DisplayName("auctionManagementMenu displays all a user's losing bids")
    public void displayUsersLostBids() {
        assertDoesNotThrow(() -> createTwoOpenAuctions("6\n\r7"));
    }

    @Test
    @DisplayName("auctionManagementMenu displays all a user's winning bids")
    public void displayUsersWonBids() {
        assertDoesNotThrow(() -> createTwoOpenAuctions("5\n\r7"));
    }
}
