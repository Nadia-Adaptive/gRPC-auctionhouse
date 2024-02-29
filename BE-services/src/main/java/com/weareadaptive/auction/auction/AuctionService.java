package com.weareadaptive.auction.auction;

import com.weareadaptive.auction.bid.Bid;
import com.weareadaptive.auction.bid.BidRequest;
import com.weareadaptive.auction.bid.BidFillStatus;
import com.weareadaptive.auction.bid.BidRepository;
import com.weareadaptive.auction.bid.BidResponse;
import com.weareadaptive.auction.exception.BusinessException;
import com.weareadaptive.auction.exception.NotFoundException;
import com.weareadaptive.auction.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AuctionService {
    AuctionRepository auctionRepository;
    UserRepository userRepository;
    private final BidRepository bidRepository;

    public AuctionService(final AuctionRepository auctionRepository, final UserRepository userRepository,
                          final BidRepository bidRepository) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
    }

    public AuctionResponse createAuction(final int ownerId, final AuctionRequest request) {
        if (!userRepository.existsById(ownerId)) {
            throw new BusinessException("User does not exist.");
        }
        final var auction = auctionRepository.save(mapToAuction(ownerId, request));
        return mapToResponse(auction);
    }

    public List<AuctionResponse> getAllAuctions(final int id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("User does not exist");
        }
        return auctionRepository.findAll(id).stream().map(a -> mapToResponse(a)).toList();
    }

    public List<AuctionResponse> getAvailableAuctions(final int id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User does not exist");
        }
        return auctionRepository.findAvailableAuctions(id).stream().map(a -> mapToResponse(a)).toList();

    }

    public AuctionResponse getAuction(final int id) {
        final var auction = auctionRepository.findById(id).orElse(null);
        if (auction == null) {
            throw new NotFoundException("Auction does not exist");
        }
        final var bids = bidRepository.findByAuction(id);
        return mapToResponse(auction, bids);
    }

    public BidResponse makeABid(final int auctionId, final int bidderId, final BidRequest body) {
        final var auction = auctionRepository.findById(auctionId).orElse(null);

        if (auction == null) {
            throw new NotFoundException("Auction does not exist");
        }
        if (!userRepository.existsById(bidderId)) {
            throw new NotFoundException("User does not exist");
        }
        if (auction.getStatus() == AuctionStatus.CLOSED) {
            throw new NotFoundException("Cannot bid on closed auction");
        }

        if (auction.getOwnerId() == bidderId) {
            throw new BusinessException("Seller cannot bid on own auction");
        }

        if (!auction.canPlaceBid(bidderId, body.offerPrice(), body.quantity())) {
            throw new BusinessException(
                    "Insufficient Bid. Offer price should be greater than asking price and quantity greater than 0.");
        }
        final var bid = bidRepository.save(new Bid(auctionId, bidderId, body.offerPrice(), body.quantity(),
                Instant.now())); // TODO: implement time provider with tests
        return new BidResponse(bid.getId(), bid.getAuctionId(), bid.getBidderId(), bid.getOfferPrice(),
                bid.getQuantity());
    }

    public AuctionResponse closeAuction(final int id, final int requesterId) {
        final var auction = auctionRepository.findById(id).orElse(null);

        if (auction == null) {
            throw new NotFoundException("Auction not found.");
        }

        if (auction.getOwnerId() != requesterId) {
            throw new BusinessException("User does not own this resource.");
        }

        final var bids = bidRepository.findByAuction(id);
        auction.close(bids);

        auctionRepository.save(auction);
        bids.forEach(b -> bidRepository.save(b));

        return mapToResponse(auction, bids);
    }

    private AuctionResponse mapToResponse(final Auction a, final List<Bid> bids) {
        final var winningBids = bids.stream()
                .filter(b -> b.getStatus() == BidFillStatus.PARTIALFILL || b.getStatus() == BidFillStatus.FILLED)
                .toList();
        final var losingBids = bids.stream()
                .filter(b -> b.getStatus() == BidFillStatus.PARTIALFILL || b.getStatus() == BidFillStatus.FILLED)
                .toList();
        return new AuctionResponse(a.getId(), a.getOwnerId(), a.getProduct(), a.getMinPrice(), a.getQuantity(),
                a.getTotalRevenue(),
                a.getTotalQuantitySold(), a.getCreatedAt(), a.getStatus(), bids, winningBids,
                losingBids);
    }

    private AuctionResponse mapToResponse(final Auction a) {
        return new AuctionResponse(a.getId(), a.getOwnerId(), a.getProduct(), a.getMinPrice(), a.getQuantity(),
                a.getTotalRevenue(),
                a.getTotalQuantitySold(), a.getCreatedAt(), a.getStatus(), null, null,
                null);
    }

    private Auction mapToAuction(final int ownerId, final AuctionRequest a) {
        return new Auction(ownerId, a.product(), a.minPrice(), a.quantity(),
                Instant.now());  // TODO: Time instant provider
    }
}
