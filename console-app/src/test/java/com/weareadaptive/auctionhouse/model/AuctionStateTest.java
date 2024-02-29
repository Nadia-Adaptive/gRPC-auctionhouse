package com.weareadaptive.auctionhouse.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.weareadaptive.auctionhouse.TestData.AUCTION1;
import static com.weareadaptive.auctionhouse.TestData.AUCTION2;
import static com.weareadaptive.auctionhouse.TestData.AUCTION3;
import static com.weareadaptive.auctionhouse.TestData.USER1;
import static com.weareadaptive.auctionhouse.TestData.USER4;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuctionStateTest {
    private static AuctionState state;

    @BeforeEach
    public void beforeEach() {
        state = new AuctionState();
        state.add(AUCTION1);
        state.add(AUCTION3);
    }

    @Test
    @DisplayName("getUserAuctions returns a list of all a user's auctions")
    public void getUsersAuctions() {
        final var allAuctions = state.getUserAuctions(USER4.getUsername());

        assertEquals(1, allAuctions.count());
    }

    @Test
    @DisplayName("getAvailableAuctions returns a list of all the auctions a user can bid on")
    public void getAvailableAuctions() {
        final var availableAuctions = state.getAvailableAuctions(USER4.getUsername());

        assertEquals(1, availableAuctions.count());
    }

    @Test
    @DisplayName("hasAuction returns true if auction exists in state")
    public void returnsTrueIfStateHasAuction() {
        assertTrue(state.hasAuction(0));
    }

    @Test
    @DisplayName("hasAuction returns false if auction does not exist in state")
    public void returnsFalseIfStateHasAuction() {
        assertFalse(state.hasAuction(3));
    }

    @Test
    @DisplayName("getAuctionsUserBidOn returns all the auctions the user's bid on")
    public void getAuctionsUserHasBidOn() {
        final var bid01 = new Bid(USER1, 12, 10, Instant.now());
        final var bid02 = new Bid(USER1, 12, 10, Instant.now());

        state.add(AUCTION2);

        AUCTION1.placeABid(bid01);
        AUCTION2.placeABid(bid02);

        AUCTION1.close();
        AUCTION2.close();

        final var auctions = state.getAuctionsUserBidOn(USER1.getUsername()).toList();

        assertEquals(2, auctions.size());

        assertTrue(auctions.get(0).getBids().anyMatch(b -> b.equals(bid01)));
        assertTrue(auctions.get(1).getBids().anyMatch(b -> b.equals(bid02)));
    }
}
