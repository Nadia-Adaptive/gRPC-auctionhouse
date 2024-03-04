package com.weareadaptive.auctionhouse.auction;

import com.weareadaptive.auctionhouse.bid.Bid;
import com.weareadaptive.auctionhouse.bid.BidRepository;
import com.weareadaptive.auctionhouse.bid.CreateBidDTO;
import com.weareadaptive.auctionhouse.exception.BusinessException;
import com.weareadaptive.auctionhouse.exception.NotFoundException;
import com.weareadaptive.auctionhouse.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.weareadaptive.auctionhouse.TestData.AUCTION1;
import static com.weareadaptive.auctionhouse.TestData.AUCTION2;
import static com.weareadaptive.auctionhouse.TestData.UID_1;
import static com.weareadaptive.auctionhouse.TestData.UID_2;
import static com.weareadaptive.auctionhouse.TestData.UID_404;
import static com.weareadaptive.auctionhouse.TestData.USER1;
import static com.weareadaptive.auctionhouse.TestData.USER2;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuctionServiceTest {
    private static final int AUCTION_ID_1 = 1;
    private static final int AUCTION_ID_2 = 2;
    private static final int AUCTION_ID_3 = 3;
    AuctionRepository auctionRepository;
    AuctionService auctionService;
    UserRepository userRepository;

    private CreateAuctionDTO createRequest(final int id) {
        return new CreateAuctionDTO(id, "product", 1.0, 10);
    }

    private CreateAuctionDTO request() {
        return createRequest(UID_1);
    }

    private CreateAuctionDTO notFoundRequest() {
        return createRequest(UID_404);
    }

    Bid bid = new Bid(0, AUCTION_ID_1, UID_2, 2.0, 10);

    AuctionServiceTest() {
        auctionRepository = mock(AuctionRepository.class);
        userRepository = mock(UserRepository.class);
        final var bidRepository = mock(BidRepository.class);

        when(userRepository.findById(UID_1)).thenReturn(USER1);
        when(userRepository.findById(UID_2)).thenReturn(USER2);

        when(userRepository.existsById(UID_2)).thenReturn(true);
        when(userRepository.existsById(UID_1)).thenReturn(true);

        when(auctionRepository.findById(AUCTION_ID_1)).thenReturn(AUCTION1);
        when(auctionRepository.findById(AUCTION_ID_2)).thenReturn(AUCTION2);
        when(auctionRepository.findById(AUCTION_ID_3)).thenReturn(
                new Auction(0, UID_2, "TEST", 1.0, 10));

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
        assertDoesNotThrow(() -> auctionService.createAuction(request()));
        assertTrue(auctionRepository.existsById(AUCTION_ID_1));
    }

    @Test
    @DisplayName("createAuction should throw when passed a userRole that doesn't exist parameters")
    void createAuctionHasInvalidSeller() {
        assertThrows(BusinessException.class, () -> auctionService.createAuction(notFoundRequest()));
    }

    @Test
    @DisplayName("getAllAuctions should return all auctions excluding the requester's")
    void getAllAuctions() {
        auctionService.closeAuction(AUCTION_ID_2, UID_2);

        final var auctions = auctionService.getAllAuctions(UID_1);

        assertEquals(0, auctions.size());
        assertTrue(auctions.stream().noneMatch((auction -> auction.getOwnerId() == UID_1)));
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
        assertEquals(AuctionStatus.OPEN, auctions.get(0).getStatus());
    }

    @Test
    @DisplayName("getAvailableAuctions should throw if userRole doesn't exist")
    void getAvailableAuctionsUserDoesntExist() {
        assertThrows(NotFoundException.class, () -> auctionService.getAvailableAuctions(UID_404));
    }

    @Test
    @DisplayName("getAuction returns an auction with a given id")
    void getAuction() {
        assertEquals(UID_1, auctionService.getAuction(1).auction().getOwnerId());
    }

    @Test
    @DisplayName("getAuction throws if the auction doesn't exist")
    void getAuctionInvalidId() {
        assertThrows(NotFoundException.class, () -> auctionService.getAuction(-10));
    }

    @Test
    @DisplayName("makeABid adds a bid to the auction for a valid bid")
    void makeABid() {
        auctionService.makeABid(new CreateBidDTO(AUCTION_ID_3, UID_1, 2.0, 10));
        assertEquals(1, auctionService.getAuction(1).bids().size());
    }

    @Test
    @DisplayName("makeABid throws when provided an auction that doesn't exist")
    void makeABidOnNonExistentAuction() {
        assertThrows(NotFoundException.class, () -> auctionService.makeABid(new CreateBidDTO(-10, UID_1, 0.1, 10)));
    }

    @Test
    @DisplayName("makeABid throws when provided a userRole that doesn't exist")
    void makeABidWithNonExistentUser() {
        assertThrows(NotFoundException.class, () -> auctionService.makeABid(new CreateBidDTO(0, UID_404, 0.1, 10)));
    }

    @Test
    @DisplayName("closeAuction closes an auction")
    void closeAuction() {
        auctionService.makeABid(new CreateBidDTO(1, UID_2, 1.1, 10));
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
