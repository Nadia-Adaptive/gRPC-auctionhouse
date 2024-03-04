package com.weareadaptive.auctionhouse.bid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.weareadaptive.auctionhouse.TestData.UID_1;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BidRepositoryTest {
    BidRepository repository;

    @BeforeEach
    void beforeEach() {
        repository.save(new Bid(1, 2, UID_1, 1.2, 10)); // TODO: Time provider
    }

    @Test
    @DisplayName("findByAuction returns a bid")
    public void findByAuction() {
        assertEquals(1, repository.findByAuction(1).size());
    }

    @Test
    @DisplayName("findByAuction returns nothing if bid doesn't exist")
    public void findByAuctionDoesNotExist() {
        assertEquals(0, repository.findByAuction(2).size());
    }
}
