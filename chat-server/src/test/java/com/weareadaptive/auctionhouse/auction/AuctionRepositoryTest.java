package com.weareadaptive.auctionhouse.auction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.weareadaptive.auctionhouse.TestData.UID_1;
import static com.weareadaptive.auctionhouse.TestData.UID_2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuctionRepositoryTest {

    private AuctionRepository repository;

    @BeforeEach
    public void beforeEach() {
        repository.save(new Auction(0, UID_1, "TEST", 1.0, 10));
    }

    @Test
    @DisplayName("getUserAuctions returns a list of all a userRole's auctions")
    public void getUsersAuctions() {
        final var allAuctions = repository.findUserAuctions(UID_1);

        assertEquals(2, allAuctions.size());
    }

    @Test
    @DisplayName("getAvailableAuctions returns a list of all the auctions a userRole can bid on")
    public void getAvailableAuctions() {
        final var availableAuctions = repository.findAvailableAuctions(UID_2);

        assertEquals(1, availableAuctions.size());
    }

    @Test
    @DisplayName("findAll with userId returns all the auctions excluding the requester's")
    void getAllAuctions() {
        final var auctions = repository.findAll(UID_2);
        assertEquals(1, auctions.size());
        assertTrue(auctions.stream().allMatch(a -> a.getOwnerId() != UID_2));
    }

    @Test
    @DisplayName("findUserAuctions returns all the requester's auctions")
    void getUserAuctions() {
        final var auctions = repository.findUserAuctions(UID_1);
        assertEquals(1, auctions.size());
        assertTrue(auctions.stream().allMatch(a -> a.getOwnerId() == UID_1));
    }
}
