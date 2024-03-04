package com.weareadaptive.auctionhouse.auction;

import com.weareadaptive.auctionhouse.bid.Bid;
import com.weareadaptive.auctionhouse.bid.BidRepository;
import com.weareadaptive.auctionhouse.bid.CreateBidDTO;
import com.weareadaptive.auctionhouse.exception.BusinessException;
import com.weareadaptive.auctionhouse.exception.NotFoundException;
import com.weareadaptive.auctionhouse.model.ResponseStatus;
import com.weareadaptive.auctionhouse.user.UserRepository;

import java.util.List;

import static com.weareadaptive.auctionhouse.auction.AuctionMapper.mapToAuction;
import static com.weareadaptive.auctionhouse.auction.AuctionMapper.mapToAuctionDTO;
import static com.weareadaptive.auctionhouse.bid.BidMapper.mapToBidDTO;

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

    public Auction createAuction(final CreateAuctionDTO request) {
        if (!userRepository.existsById(request.ownerId())) {
            throw new NotFoundException("User does not exist.");
        }

        final var auction = auctionRepository.save(mapToAuction(auctionRepository.nextId(), request));
        if (auction == null) {
            throw new BusinessException("Something went wrong.");
        }
        return auction;
    }

    public List<Auction> getAllAuctions(final int id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User does not exist");
        }
        return auctionRepository.findAll(id);
    }

    public List<Auction> getAvailableAuctions(final int id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User does not exist");
        }
        return auctionRepository.findAvailableAuctions(id);

    }

    public AuctionDTO getAuction(final int id) {
        final var auction = auctionRepository.findById(id);
        if (auction == null) {
            throw new NotFoundException("Auction does not exist");
        }
        final var bids = bidRepository.findByAuction(id);
        return mapToAuctionDTO(auction, bids);
    }

    public BidDTO makeABid(final CreateBidDTO dto) {
        final var auction = auctionRepository.findById(dto.auctionId());

        if (auction == null) {
            throw new NotFoundException("Auction not found.");
        }
        if (!userRepository.existsById(dto.bidderId())) {
            throw new NotFoundException("User does not exist");
        }
        if (auction.getStatus() == AuctionStatus.CLOSED) {
            throw new NotFoundException("Cannot bid on closed auction");
        }

        if (auction.getOwnerId() == dto.bidderId()) {
            throw new BusinessException("Seller cannot bid on own auction");
        }

        if (!auction.canPlaceBid(dto.bidderId(), dto.offerPrice(), dto.quantity())) {
            throw new BusinessException(
                    "Insufficient Bid. Offer price should be greater than asking price and quantity greater than 0.");
        }
        final var bid = bidRepository.save(
                new Bid(bidRepository.nextId(), dto.auctionId(), dto.bidderId(), dto.offerPrice(),
                        dto.quantity())); // TODO: implement time provider with tests

        if (bid == null) {
            throw new BusinessException(ResponseStatus.INTERNAL_ERROR);
        }
        return mapToBidDTO(bid);
    }

    public AuctionDTO closeAuction(final int id, final int requesterId) {
        final var auction = auctionRepository.findById(id);
        final var requestValidity = checkRequestValidity(auction, requesterId);

        if (requestValidity != null) {
            throw (RuntimeException) requestValidity.getCause();
        }

        final var bids = bidRepository.findByAuction(id);
        auction.close(bids);

        auctionRepository.save(auction);
        bids.forEach(b -> bidRepository.save(b));

        return mapToAuctionDTO(auction, bids);
    }

    public AuctionDTO deleteAuction(final DeleteAuctionDTO dto) {
        final var auction = auctionRepository.findById(dto.auctionId());

        final var requestValidity = checkRequestValidity(auction, dto.auctionId());

        if (requestValidity != null) {
            throw (RuntimeException) requestValidity.getCause();
        }

        return mapToAuctionDTO(auctionRepository.remove(dto.auctionId()));
    }

    private Exception checkRequestValidity(final Auction auction, final int requesterId) {
        if (auction == null) {
            return new NotFoundException("Auction not found.");
        }

        if (auction.getOwnerId() != requesterId) {
            return new BusinessException("User does not own this resource.");
        }

        return null;
    }
}
