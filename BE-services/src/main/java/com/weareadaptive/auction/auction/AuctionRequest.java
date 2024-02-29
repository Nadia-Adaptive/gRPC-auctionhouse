package com.weareadaptive.auction.auction;

public record AuctionRequest(String product, double minPrice, int quantity) {
}
