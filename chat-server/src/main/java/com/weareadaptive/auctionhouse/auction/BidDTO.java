package com.weareadaptive.auctionhouse.auction;

import com.weareadaptive.auctionhouse.bid.BidFillStatus;

public record BidDTO(int bidId, int auctionId, int bidderId, double offerPrice, int quantity, BidFillStatus fillStatus,
                     int totalQuantityFilled) {
}
