package com.weareadaptive.auctionhouse.bid;

import com.weareadaptive.auctionhouse.Auction.gRPCBidService;
import com.weareadaptive.auctionhouse.Auction.gRPCBidService.BidResponse;
import com.weareadaptive.auctionhouse.Auction.gRPCBidService.BidStatusValue;
import com.weareadaptive.auctionhouse.Auction.gRPCBidService.BidsResponse;
import com.weareadaptive.auctionhouse.auction.BidDTO;

import java.util.List;

public final class BidMapper {
    private BidMapper() {
    }

    public static BidResponse mapToBidResponse(final BidDTO bid) {
        return BidResponse.newBuilder()
                .setBidId(bid.bidId())
                .setAuctionId(bid.auctionId())
                .setBidderId(bid.bidderId())
                .setBidStatus(
                        BidStatusValue.valueOf(bid.fillStatus().name())
                )
                .setOfferPrice(bid.offerPrice())
                .setQuantity(bid.quantity())
                .build();
    }

    public static BidResponse mapToBidResponse(final Bid bid) {
        return BidResponse.newBuilder()
                .setBidId(bid.getId())
                .setAuctionId(bid.getAuctionId())
                .setBidderId(bid.getBidderId())
                .setBidStatus(
                        BidStatusValue.valueOf(bid.getStatus().name())
                )
                .setOfferPrice(bid.getOfferPrice())
                .setQuantity(bid.getQuantity())
                .build();
    }

    public static CreateBidDTO mapToBidRequest(final int bidderId, final gRPCBidService.CreateBidRequest request) {
        return new CreateBidDTO(bidderId, request.getAuctionId(), request.getOfferPrice(), request.getQuantity());
    }

    public static BidsResponse mapToBidsResponse(final List<Bid> bids) {
        return bids == null ? BidsResponse.newBuilder().build() : BidsResponse.newBuilder()
                .addAllBids(mapToBidResponseList(bids)).build();
    }

    public static List<BidResponse> mapToBidResponseList(final List<Bid> bids) {
        return bids.stream().map(BidMapper::mapToBidResponse).toList();
    }

    public static BidDTO mapToBidDTO(final Bid bid) {
        return new BidDTO(bid.getId(),
                bid.getAuctionId(),
                bid.getBidderId(),
                bid.getOfferPrice(),
                bid.getQuantity(),
                bid.getStatus(),
                bid.getQuantityFilled());
    }
}
