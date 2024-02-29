package com.weareadaptive.auctionhouse.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static com.weareadaptive.auctionhouse.TestData.USER1;
import static com.weareadaptive.auctionhouse.TestData.USER2;
import static com.weareadaptive.auctionhouse.TestData.USER3;
import static com.weareadaptive.auctionhouse.TestData.USER4;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuctionTest {
    private static Auction auction;

    private static void createTwoWinningBidsAndOneLostBid() {
        final var bid01 = new Bid(USER1, 1.2d, 3, Instant.now());
        final var bid02 = new Bid(USER2, 1.2d, 7, Instant.now());
        final var bid03 = new Bid(USER3, 1.2d, 7, Instant.now());

        auction.placeABid(bid01);
        auction.placeABid(bid02);
        auction.placeABid(bid03);
    }

    @BeforeEach
    public void beforeEach() {
        auction = new Auction(0, USER4, "JPYUSD", 1.00, 10);
    }

    @Test
    @DisplayName("Throws an exception when seller is null")
    void throwExceptionSellerIsNull() {
        assertThrows(BusinessException.class, () -> new Auction(0, null, "JPYUSD", 1.00, 10));
    }

    @Test
    @DisplayName("Throws an exception when quantity is 0 or less")
    void throwExceptionQuantityIsZeroOrLess() {
        assertThrows(BusinessException.class, () -> new Auction(0, USER1, "JPYUSD", 1.00, 0));
        assertThrows(BusinessException.class, () -> new Auction(0, USER1, "JPYUSD", 1.00, -10));
    }

    @Test
    @DisplayName("Throws an exception when minPrice is 0 or less")
    void throwExceptionMinPriceIsZeroOrLess() {
        assertThrows(BusinessException.class, () -> new Auction(0, USER1, "JPYUSD", 0, 10));
        assertThrows(BusinessException.class, () -> new Auction(0, USER1, "JPYUSD", -1, 10));
    }

    @Test
    @DisplayName("Throws an exception when product is null or empty")
    void throwExceptionProductIsNullOrEmpty() {
        assertThrows(BusinessException.class, () -> new Auction(0, USER1, "", 1.00, 10));
        assertThrows(BusinessException.class, () -> new Auction(0, USER1, null, 1.00, 10));
    }

    @Test
    @DisplayName("placeABid should add a bid for the auction")
    public void placeABid() {
        final var bid = new Bid(USER1, 1.2d, 5, Instant.now());
        auction.placeABid(bid);

        assertEquals(1, auction.getBids().count());
    }

    @Test
    @DisplayName("placeABid throws an exception when the bid doesn't meet the auction's minimum price")
    public void placeABidThrowsExceptionWhenBidDoesntMeetMinPrice() {
        final var bid01 = new Bid(USER1, 0.2d, 6, Instant.now());

        assertThrows(BusinessException.class, () -> auction.placeABid(bid01));
    }

    @Test
    @DisplayName("placeABid throws an exception when the seller tries to bid on their own auction")
    public void placeABidThrowsExceptionWhenSellerTriesToBid() {
        final var bid01 = new Bid(USER4, 0.2d, 6, Instant.now());

        assertThrows(BusinessException.class, () -> auction.placeABid(bid01));
    }

    @Test
    @DisplayName("closeAuction closes the auction when's one bid")
    public void closeAuctionWithOneBid() {
        final var winningBid = new Bid(USER2, 10d, 5, Instant.now());
        auction.placeABid(winningBid);
        assertTrue(auction.getStatus() == AuctionStatus.OPEN);

        auction.close();
        assertEquals(auction.getStatus(), AuctionStatus.CLOSED);
        assertEquals(auction.getWinningBids().findFirst().get(), winningBid);
    }

    @Test
    @DisplayName("closeAuction fills earliest offer when multiple bids have same price")
    public void closeAuctionFillsWithEarliestBid() {
        final var bid01 = new Bid(USER1, 1.2d, 5, Instant.now());
        final var bid02 = new Bid(USER2, 1.2d, 5, Instant.now());
        auction.placeABid(bid01);
        auction.placeABid(bid02);

        assertTrue(auction.getStatus() == AuctionStatus.OPEN);

        auction.close();
        final var winningBids = auction.getWinningBids().toList();
        assertEquals(AuctionStatus.CLOSED, auction.getStatus());
        assertEquals(bid01, winningBids.get(0));
        assertEquals(bid02, winningBids.get(1));
    }

    @Test
    @DisplayName("closeAuction fills as many bids as possible before closing")
    public void closeAuctionFillsWithMultipleBids() {
        createTwoWinningBidsAndOneLostBid();

        assertTrue(auction.getStatus() == AuctionStatus.OPEN);

        auction.close();
        assertEquals(AuctionStatus.CLOSED, auction.getStatus());
        assertEquals(2, auction.getWinningBids().count());
    }

    @Test
    @DisplayName("closeAuction fills as many bids as possible including bids it can only partially fill")
    public void closesAuctionFillsWithMultipleBidsAndOnePartialBid() {
        final var bid01 = new Bid(USER1, 1.2d, 3, Instant.now());
        final var bid02 = new Bid(USER2, 1.2d, 3, Instant.now());
        final var bid03 = new Bid(USER3, 1.2d, 6, Instant.now());

        auction.placeABid(bid01);
        auction.placeABid(bid02);
        auction.placeABid(bid03);

        assertTrue(auction.getStatus() == AuctionStatus.OPEN);

        auction.close();
        assertEquals(AuctionStatus.CLOSED, auction.getStatus());
        assertEquals(3, auction.getWinningBids().count());
    }

    @Test
    @DisplayName("closeAuction fills as many bids as possible by highest price")
    public void closeAuctionFillsByHighestPrice() {
        final var bid03 = new Bid(USER3, 12d, 1, Instant.now());
        final var bid01 = new Bid(USER1, 1.2d, 3, Instant.now());
        final var bid02 = new Bid(USER2, 1.24d, 5, Instant.now());

        auction.placeABid(bid01);
        auction.placeABid(bid02);
        auction.placeABid(bid03);

        auction.close();

        final var winningBids = auction.getWinningBids().toList();

        assertEquals(AuctionStatus.CLOSED, auction.getStatus());
        assertEquals(bid03, winningBids.get(0));
        assertEquals(bid02, winningBids.get(1));
        assertEquals(bid01, winningBids.get(2));
    }

    @Test
    @DisplayName("closeAuction updates the totalQuantitySold and totalRevenue")
    public void closeAuctionUpdatesTotalQuantitySoldAndTotalRevenue() {
        final var bid01 = new Bid(USER1, 1.2d, 15, Instant.now());

        auction.placeABid(bid01);

        auction.close();

        assertEquals(10, auction.getTotalQuantitySold());
        assertEquals(new BigDecimal(1.2d * 10).doubleValue(), auction.getTotalRevenue());
    }

    @Test
    @DisplayName("closeAuction stores all winning and losing bids")
    public void closeAuctionStoresWonAndLostBids() {
        createTwoWinningBidsAndOneLostBid();

        auction.close();

        assertEquals(2, auction.getWinningBids().count());
        assertEquals(1, auction.getLosingBids().count());
    }

    @Test
    @DisplayName("hasUserBid returns true when user has bid and false if they haven't")
    public void hasUserBid() {
        createTwoWinningBidsAndOneLostBid();

        assertTrue(auction.hasUserBid(USER1.getUsername()));
        assertFalse(auction.hasUserBid(USER4.getUsername()));
    }
}
