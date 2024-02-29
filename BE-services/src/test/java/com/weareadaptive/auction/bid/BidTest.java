package com.weareadaptive.auction.bid;

import com.weareadaptive.auction.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BidTest {
    @Test
    @DisplayName("Throws an exception when offerPrice is 0 or less")
    void throwExceptionOfferIsZeroOrLess() {
        assertThrows(BusinessException.class, () -> new Bid(1, 1, -110, 10, Instant.now()));
        assertThrows(BusinessException.class, () -> new Bid(1, 1, 0, 10, Instant.now()));
    }

    @Test
    @DisplayName("Throws an exception when quantity is 0 or less")
    void throwExceptionQuantityIsZeroOrLess() {
        assertThrows(BusinessException.class, () -> new Bid(1, 1, 10, 0, Instant.now()));
        assertThrows(BusinessException.class, () -> new Bid(1, 1, 10, -10, Instant.now()));
    }

    @Test
    @DisplayName("A new bid's status is pending")
    void newBidStatusIsPending() {
        assertEquals(BidFillStatus.PENDING, new Bid(1, 1, 1, 10, Instant.now()).getStatus());
    }

    @Test
    @DisplayName("CompareTo should return the correct integers when compared")
    void compareTo() {
        Bid bid01 = new Bid(1, 1, 1.1, 10, Instant.now());
        Bid bid02 = new Bid(1, 1, 1.0, 10, Instant.now());
        Bid bid03 = new Bid(1, 1, 15.0, 10, Instant.now());

        assertEquals(-1, bid01.compareTo(bid03));
        assertEquals(1, bid01.compareTo(bid02));
    }

    @Test
    @DisplayName("fillBid should correctly update the bid state")
    void fillBid() {
        Bid bid01 = new Bid(1, 1, 1.1, 10, Instant.now());
        Bid bid02 = new Bid(1, 1, 1.1, 10, Instant.now());
        Bid bid03 = new Bid(1, 1, 1.1, 10, Instant.now());

        bid01.fillBid(10);
        bid02.fillBid(5);
        bid03.fillBid(0);

        assertEquals(BidFillStatus.FILLED, bid01.getStatus());
        assertEquals(10, bid01.getQuantityFilled());

        assertEquals(BidFillStatus.PARTIALFILL, bid02.getStatus());
        assertEquals(5, bid02.getQuantityFilled());

        assertEquals(BidFillStatus.UNFILLED, bid03.getStatus());
        assertEquals(0, bid03.getQuantityFilled());
    }

    @Test
    @DisplayName("fillBid should throw a BusinessException if it is called on a non-pending bid")
    void fillBidThrows() {
        Bid bid01 = new Bid(1, 1, 1.1, 10, Instant.now());

        bid01.fillBid(10);
        assertThrows(BusinessException.class, () -> bid01.fillBid(30));
    }
}
