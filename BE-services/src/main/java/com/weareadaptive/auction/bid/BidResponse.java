package com.weareadaptive.auction.bid;

public record BidResponse(int id, int auctionId, int bidderId, double offerPrice, int quantity) {
}
