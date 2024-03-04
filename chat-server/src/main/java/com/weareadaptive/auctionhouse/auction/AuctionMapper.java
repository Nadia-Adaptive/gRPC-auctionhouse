package com.weareadaptive.auctionhouse.auction;

import com.weareadaptive.auctionhouse.Auction.gRPCAuctionService.AuctionResponse;
import com.weareadaptive.auctionhouse.Auction.gRPCAuctionService.AuctionStatusValue;
import com.weareadaptive.auctionhouse.Auction.gRPCAuctionService.CreateAuctionRequest;
import com.weareadaptive.auctionhouse.Auction.gRPCAuctionService.DeleteAuctionRequest;

import com.weareadaptive.auctionhouse.bid.Bid;
import com.weareadaptive.auctionhouse.bid.BidFillStatus;


import java.util.List;

import static com.weareadaptive.auctionhouse.bid.BidMapper.mapToBidResponseList;

public final class AuctionMapper {
    private AuctionMapper() {
    }

    public static CreateAuctionDTO mapToCreateAuctionDTO(final int ownerId, final CreateAuctionRequest request) {
        return new CreateAuctionDTO(ownerId, request.getProduct(), request.getMinOfferPrice(),
                request.getQuantity());
    }

    public static Auction mapToAuction(final int auctionId, final CreateAuctionDTO a) {
        return new Auction(auctionId, a.ownerId(), a.product(), a.minOfferPrice(),
                a.quantity());  // TODO: Time instant provider
    }

    public static AuctionDTO mapToAuction(final Auction a) {
        return mapToAuctionDTO(a, null);
    }
    public static AuctionDTO mapToAuctionDTO(final Auction a, final List<Bid> bids) {
        if (bids != null) {
            final var winningBids = bids.stream()
                    .filter(b -> b.getStatus() == BidFillStatus.PARTIALFILL || b.getStatus() == BidFillStatus.FILLED)
                    .toList();
            final var losingBids = bids.stream()
                    .filter(b -> b.getStatus() == BidFillStatus.PARTIALFILL || b.getStatus() == BidFillStatus.FILLED)
                    .toList();
            return new AuctionDTO(a, bids, winningBids, losingBids);
        }
        return new AuctionDTO(a, null, null, null);
    }

    public static AuctionDTO mapToAuctionDTO(final Auction a) {
        return mapToAuctionDTO(a, null);
    }

    public static AuctionResponse mapToAuctionResponse(final AuctionDTO dto) {
        final var auction = dto.auction();
        final var bids = dto.bids();

        return AuctionResponse.newBuilder()
                .setAuctionId(auction.getAuctionId())
                .setAuctionStatus(
                        AuctionStatusValue.valueOf(auction.getStatus().name()))
                .setQuantity(auction.getQuantity())
                .setMinOfferPrice(auction.getMinOfferPrice())
                .addAllBids(bids == null ? List.of() : mapToBidResponseList(bids))
                .build();
    }

    public static DeleteAuctionDTO mapToDeleteAuctionDTO(final int userId, final DeleteAuctionRequest request) {
        return new DeleteAuctionDTO(userId, request.getAuctionId());
    }
}
