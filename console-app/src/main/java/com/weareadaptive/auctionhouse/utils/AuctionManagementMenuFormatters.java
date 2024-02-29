package com.weareadaptive.auctionhouse.utils;

import com.weareadaptive.auctionhouse.model.Auction;
import com.weareadaptive.auctionhouse.model.Bid;

public class AuctionManagementMenuFormatters {
    public static String formatBuyerBidEntry(final Auction a, final Bid b) {
        return "{ Auction Owner: %s | Auction Product: %s | Offer price: %.3f | Quantity: %d | Status: %s }".formatted(
                a.getSeller().getUsername(), a.getSymbol(),
                b.getPrice(), b.getQuantity(),
                b.getStatus().toString());
    }

    public static String formatAuctionEntry(final Auction a) {
        return "Auction Id: %d | Product: %s | Owner: %s\n================================================".formatted(
                a.getId(), a.getSymbol(), a.getSeller().getUsername());
    }

    public static String formatSellerBidEntry(final Bid b) {
        return "\t{ Buyer: %s | Offer price: %.3f | Quantity: %d }".formatted(
                b.getBuyer().getUsername(), b.getPrice(),
                b.getQuantity(), b.getStatus().toString());
    }

    public static String formatOpenAuctionSummary(final Auction auction) {
        return """
                ========================================
                Id: %d%nSymbol: %s%nStatus: %s%nAll Bids: \n%s
                ========================================""".formatted(
                auction.getId(), auction.getSymbol(),
                auction.getStatus().toString(),
                auction.getBids()
                        .map(AuctionManagementMenuFormatters::formatSellerBidEntry)
                        .reduce((String acc, String val) -> String.join("\n", acc, val)
                        ).orElse(""));
    }

    public static String formatClosedAuctionSummary(final Auction auction) {
        return """
                ========================================
                Id: %d%nSymbol: %s%nStatus: %s%nAll Bids: \n%s
                ========================================
                Total revenue: %.3f\nTotal Quantity Sold: %d\n==============\nWinning bids:\n%s\n""".formatted(
                auction.getId(),
                auction.getSymbol(),
                auction.getStatus().toString(),
                auction.getBids()
                        .map(AuctionManagementMenuFormatters::formatSellerBidEntry)
                        .reduce((String acc, String val) -> String.join("\n", acc, val)
                        ).orElse(""), auction.getTotalRevenue(), auction.getTotalQuantitySold(),
                auction.getWinningBids().map(b -> "\tUser: %s\n\tTotal quantity filled: %d\n\tPrice: %.3f".formatted(
                                b.getBuyer().getUsername(),
                                b.getQuantityFilled(), b.getPrice()))
                        .reduce((String acc, String val) -> String.join("\n", acc, val)
                        ).orElse(""));
    }
}
