package com.weareadaptive.auction.bid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public record BidRequest(double offerPrice, int quantity) {
}
