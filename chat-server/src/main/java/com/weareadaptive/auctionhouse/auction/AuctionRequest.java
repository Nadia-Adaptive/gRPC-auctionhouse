package com.weareadaptive.auctionhouse.auction;

public record AuctionRequest(String product, double minPrice, int quantity) {
}
