package com.weareadaptive.auctionhouse.auction;

import com.weareadaptive.auctionhouse.bid.Bid;
import com.weareadaptive.auctionhouse.bid.BidFillStatus;
import com.weareadaptive.auctionhouse.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.weareadaptive.auctionhouse.TestData.UID_1;
import static com.weareadaptive.auctionhouse.TestData.UID_2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuctionTest {
    private static final String TEST = "TEST";
    private static Auction auction;

    List<Bid> getBids() {
        return List.of(new Bid(1, 1, UID_1, 3.2d, 3),
                new Bid(1, 2, UID_2, 1.2d, 7),
                new Bid(1, 3, UID_1, 1.2d, 7));
    }

    List<Bid> getWinningBids(final List<Bid> bids) {
        return bids.stream()
                .filter(b -> b.getStatus() == BidFillStatus.FILLED || b.getStatus() == BidFillStatus.PARTIALFILL)
                .toList();
    }

    @BeforeEach
    void beforeEach() {
        auction = new Auction(0, UID_1, TEST, 1.0, 10);
    }

    @Test
    @DisplayName("Throws an exception when quantity is 0 or less")
    void throwExceptionQuantityIsZeroOrLess() {
        assertThrows(BusinessException.class, () -> new Auction(0, UID_1, TEST, 1.00, 0));
        assertThrows(BusinessException.class, () -> new Auction(0, UID_1, TEST, 1.00, -10));
    }

    @Test
    @DisplayName("Throws an exception when minPrice is 0 or less")
    void throwExceptionMinPriceIsZeroOrLess() {
        assertThrows(BusinessException.class, () -> new Auction(1, UID_1, TEST, -1.00, 10));
        assertThrows(BusinessException.class, () -> new Auction(1, UID_1, TEST, 0, 10));
    }

    @Test
    @DisplayName("Throws an exception when product is null or empty")
    void throwExceptionProductIsNullOrEmpty() {
        assertThrows(BusinessException.class, () -> new Auction(1, UID_1, "", 1.00, 0));
        assertThrows(BusinessException.class, () -> new Auction(1, UID_1, null, 1.00, -10));
    }

    @Test
    @DisplayName("Throws an exception when createdAt is null")
    void throwExceptionCreatedAtIsNull() {
        assertThrows(BusinessException.class, () -> new Auction(1, UID_1, TEST, 1.00, 0));
    }

    @Test
    @DisplayName("canPlaceBid should return true for a valid bid")
    public void canPlaceBid() {
        final var bid = new Bid(1, 1, UID_1, 1.2d, 5);
        assertTrue(auction.canPlaceBid(bid.getBidderId(), bid.getOfferPrice(), bid.getQuantity()));
    }

    @Test
    @DisplayName("canPlaceBid returns false when the bid doesn't meet the auction's minimum price")
    public void canPlaceBidReturnFalse() {
        final var bid = new Bid(1, 0, UID_1, 0.2d, 6);
        assertFalse(auction.canPlaceBid(bid.getBidderId(), bid.getOfferPrice(), bid.getQuantity()));
    }

    @Test
    @DisplayName("closeAuction closes the auction when's one bid")
    public void closeAuctionWithOneBid() {
        final var winningBid = new Bid(1, 1, UID_2, 10d, 5);

        assertSame(auction.getStatus(), AuctionStatus.OPEN);

        auction.close(List.of(winningBid));
        assertEquals(auction.getStatus(), AuctionStatus.CLOSED);
    }

    @Test
    @DisplayName("closeAuction fills earliest offer when multiple bids have same price")
    public void closeAuctionFillsWithEarliestBid() {
        final var bid01 = new Bid(1, 1, UID_1, 1.2d, 5);
        final var bid02 = new Bid(1, 2, UID_2, 1.2d, 5);

        assertSame(auction.getStatus(), AuctionStatus.OPEN);

        auction.close(List.of(bid01, bid02));
        assertEquals(AuctionStatus.CLOSED, auction.getStatus());
        assertEquals(BidFillStatus.FILLED, bid01.getStatus());
        assertEquals(BidFillStatus.FILLED, bid02.getStatus());
        assertTrue(bid01.getUpdatedAt().isBefore(bid02.getUpdatedAt()));
    }

    @Test
    @DisplayName("closeAuction fills as many bids as possible before closing")
    public void closeAuctionFillsWithMultipleBids() {
        final var bids = getBids();

        assertSame(auction.getStatus(), AuctionStatus.OPEN);

        auction.close(bids);
        assertEquals(AuctionStatus.CLOSED, auction.getStatus());
        assertEquals(2, getWinningBids(bids).size());
    }

    @Test
    @DisplayName("closeAuction fills as many bids as possible including bids it can only partially fill")
    public void closesAuctionFillsWithMultipleBidsAndOnePartialBid() {
        final var bids = getBids();

        assertSame(auction.getStatus(), AuctionStatus.OPEN);

        auction.close(bids);
        assertEquals(AuctionStatus.CLOSED, auction.getStatus());
        assertEquals(2, getWinningBids(bids).size());
    }

    @Test
    @DisplayName("closeAuction fills as many bids as possible by highest price")
    public void closeAuctionFillsByHighestPrice() {
        final var bids = getBids();

        auction.close(bids);

        final var winningBids = getWinningBids(bids);

        assertEquals(AuctionStatus.CLOSED, auction.getStatus());
        assertEquals(1, winningBids.get(0).getBidderId());
        assertEquals(2, winningBids.get(1).getBidderId());
    }

    @Test
    @DisplayName("closeAuction updates the totalQuantitySold and totalRevenue")
    public void closeAuctionUpdatesTotalQuantitySoldAndTotalRevenue() {
        auction.close(List.of(new Bid(1, 1, UID_1, 1.2d, 15)));

        assertEquals(10, auction.getTotalQuantitySold());
        assertEquals(BigDecimal.valueOf(1.2d * 10), auction.getTotalRevenue());
    }

    @Test
    @DisplayName("closeAuction calculates all won and lost bids")
    public void closeAuctionStoresWonAndLostBids() {
        final var bids = getBids();

        auction.close(bids);

        final var winningBids = getWinningBids(bids);

        assertEquals(2, winningBids.size());
        assertEquals(1, bids.stream().filter(b -> b.getStatus() == BidFillStatus.UNFILLED).count());
    }
}
