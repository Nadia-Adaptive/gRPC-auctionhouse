package com.weareadaptive.auctionhouse.auction;

public record CreateAuctionDTO(int ownerId, String product, double minOfferPrice, int quantity) {
}
