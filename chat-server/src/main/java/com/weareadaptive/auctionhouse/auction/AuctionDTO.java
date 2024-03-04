package com.weareadaptive.auctionhouse.auction;

import com.weareadaptive.auctionhouse.bid.Bid;

import java.util.List;

public record AuctionDTO(Auction auction, List<Bid> bids, List<Bid> winningBids, List<Bid> losingBids) {
}
