package com.weareadaptive.auction.auction;

import com.weareadaptive.auction.bid.Bid;
import com.weareadaptive.auction.bid.BidRequest;
import com.weareadaptive.auction.bid.BidRepository;
import com.weareadaptive.auction.exception.BusinessException;
import com.weareadaptive.auction.exception.NotFoundException;
import com.weareadaptive.auction.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.weareadaptive.auction.TestData.AUCTION1;
import static com.weareadaptive.auction.TestData.AUCTION2;
import static com.weareadaptive.auction.TestData.UID_1;
import static com.weareadaptive.auction.TestData.UID_2;
import static com.weareadaptive.auction.TestData.UID_404;
import static com.weareadaptive.auction.TestData.USER1;
import static com.weareadaptive.auction.TestData.USER2;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuctionServiceTest {
    private final int AUCTION_ID_1 = 1;
    private final int AUCTION_ID_2 = 2;
    private static final int AUCTION_ID_3 = 3;
    AuctionRepository auctionRepository;
    AuctionService auctionService;
    UserRepository userRepository;

    AuctionRequest request = new AuctionRequest("product", 1.0, 10);

    Bid bid = new Bid(1, UID_2, 2.0, 10, Instant.now());


    AuctionServiceTest() {
        auctionRepository = mock(AuctionRepository.class);
        userRepository = mock(UserRepository.class);
        final var bidRepository = mock(BidRepository.class);

        when(userRepository.findById(UID_1)).thenReturn(Optional.of(USER1));
        when(userRepository.findById(UID_2)).thenReturn(Optional.of(USER2));

        when(userRepository.existsById(UID_2)).thenReturn(true);
        when(userRepository.existsById(UID_1)).thenReturn(true);

        when(auctionRepository.findById(AUCTION_ID_1)).thenReturn(Optional.of(AUCTION1));
        when(auctionRepository.findById(AUCTION_ID_2)).thenReturn(Optional.of(AUCTION2));
        when(auctionRepository.findById(AUCTION_ID_3)).thenReturn(
                Optional.of(new Auction(UID_2, "TEST", 1.0, 10, Instant.now())));

        when(auctionRepository.existsById(AUCTION_ID_1)).thenReturn(true);
        when(auctionRepository.existsById(AUCTION_ID_2)).thenReturn(true);
        when(auctionRepository.existsById(AUCTION_ID_3)).thenReturn(true);

        when(auctionRepository.findAvailableAuctions(UID_1)).thenReturn(List.of(AUCTION2));
        when(auctionRepository.findAvailableAuctions(UID_2)).thenReturn(List.of(AUCTION1));

        when(auctionRepository.save(any(Auction.class))).thenReturn(AUCTION1);

        when(bidRepository.save(any(Bid.class))).thenReturn(bid);
        when(bidRepository.findByAuction(any(Integer.class))).thenReturn(List.of(bid));

        auctionService = new AuctionService(auctionRepository, userRepository, bidRepository);
    }

    @Test
    @DisplayName("createAuction should not throw when adding new auction")
    void createAuction() {
        assertDoesNotThrow(() -> auctionService.createAuction(UID_1, request));
        assertTrue(auctionRepository.existsById(AUCTION_ID_1));
    }

    @Test
    @DisplayName("createAuction should throw when passed a userRole that doesn't exist parameters")
    void createAuctionHasInvalidSeller() {
        assertThrows(BusinessException.class, () -> auctionService.createAuction(UID_404, request));
    }

    @Test
    @DisplayName("getAllAuctions should return all auctions excluding the requester's")
    void getAllAuctions() {
        auctionService.closeAuction(AUCTION_ID_2, UID_2);

        final var auctions = auctionService.getAllAuctions(UID_1);

        assertEquals(0, auctions.size());
        assertTrue(auctions.stream().noneMatch((auction -> auction.ownerId() == UID_1)));
    }

    @Test
    @DisplayName("getAllAuctions should throw when passed a userRole that doesn't exist parameters")
    void getAllAuctionsUserDoesntExist() {
        assertThrows(NotFoundException.class, () -> auctionService.getAllAuctions(UID_404));
    }

    @Test
    @DisplayName("getAvailableAuctions should return all open auctions excluding the requester's")
    void getAvailableAuctions() {
        auctionService.closeAuction(AUCTION_ID_1, UID_1);

        final var auctions = auctionService.getAvailableAuctions(UID_1);
        assertEquals(1, auctions.size());
        assertEquals(AuctionStatus.OPEN, auctions.get(0).status());
    }

    @Test
    @DisplayName("getAvailableAuctions should throw if userRole doesn't exist")
    void getAvailableAuctionsUserDoesntExist() {
        assertThrows(NotFoundException.class, () -> auctionService.getAvailableAuctions(UID_404));
    }

    @Test
    @DisplayName("getAuction returns an auction with a given id")
    void getAuction() {
        assertEquals(UID_1, auctionService.getAuction(1).ownerId());
    }

    @Test
    @DisplayName("getAuction throws if the auction doesn't exist")
    void getAuctionInvalidId() {
        assertThrows(NotFoundException.class, () -> auctionService.getAuction(-10));
    }

    @Test
    @DisplayName("makeABid adds a bid to the auction for a valid bid")
    void makeABid() {
        auctionService.makeABid(AUCTION_ID_3, UID_1, new BidRequest(2.0, 10));
        assertEquals(1, auctionService.getAuction(1).bids().size());
    }

    @Test
    @DisplayName("makeABid throws when provided an auction that doesn't exist")
    void makeABidOnNonExistentAuction() {
        assertThrows(NotFoundException.class, () -> auctionService.makeABid(-10, UID_1, new BidRequest(0.1, 10)));
    }

    @Test
    @DisplayName("makeABid throws when provided a userRole that doesn't exist")
    void makeABidWithNonExistentUser() {
        assertThrows(NotFoundException.class, () -> auctionService.makeABid(0, UID_404, new BidRequest(0.1, 10)));
    }

    @Test
    @DisplayName("closeAuction closes an auction")
    void closeAuction() {
        auctionService.makeABid(1, UID_2, new BidRequest(1.1, 10));
        assertDoesNotThrow(() -> auctionService.closeAuction(1, UID_1));
        assertEquals(1, auctionService.getAuction(1).winningBids().size());
    }

    @Test
    @DisplayName("closeAuction throws if provided an invalid auction id")
    void closeAuctionProvidedInvalidId() {
        assertThrows(NotFoundException.class, () -> auctionService.closeAuction(-1, UID_1));
    }

    @Test
    @DisplayName("closeAuction throws if userRole requesting close does not own the auction")
    void closeAuctionClosedByNonOwner() {
        assertThrows(BusinessException.class, () -> auctionService.closeAuction(1, UID_2));
    }
}
