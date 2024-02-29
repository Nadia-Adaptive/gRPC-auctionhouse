package com.weareadaptive.auction.auction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.weareadaptive.auction.bid.Bid;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties
public record AuctionResponse(int id, int ownerId, String product, double minPrice, int quantity, BigDecimal totalRevenue,
                              int totalQuantitySold, Instant createdAt, AuctionStatus status,
                              List<Bid> bids, List<Bid> winningBids, List<Bid> losingBids) {
}
