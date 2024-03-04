package com.weareadaptive.auctionhouse.bid;
public record CreateBidDTO(int auctionId, int bidderId, double offerPrice, int quantity) {
}
