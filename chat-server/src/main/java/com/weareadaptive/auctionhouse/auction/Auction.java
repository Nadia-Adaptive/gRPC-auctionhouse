package com.weareadaptive.auctionhouse.auction;

import com.weareadaptive.auctionhouse.exception.BusinessException;
import com.weareadaptive.auctionhouse.bid.Bid;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import static com.weareadaptive.auctionhouse.utils.StringUtil.isNullOrEmpty;

public class Auction {
    private int auctionId;
    private String product;
    private double minOfferPrice;
    private int quantity;
    private int ownerId;
    private AuctionStatus status;
    private Instant closedAt;;
    private BigDecimal totalRevenue;
    private int totalQuantitySold;

    public Auction(final int auctionId, final int ownerId, final String symbol, final double minPrice,
                   final int quantity) {
        if (isNullOrEmpty(symbol)) {
            throw new BusinessException("Symbol cannot be empty!");
        }

        if (minPrice <= 0d) {
            throw new BusinessException("Price cannot be less than or equal to zero.");
        }
        if (quantity <= 0) {
            throw new BusinessException("Quantity cannot be less than or equal to zero.");
        }

        this.auctionId = auctionId;
        this.ownerId = ownerId;
        this.product = symbol;
        this.minOfferPrice = minPrice;
        this.quantity = quantity;
        this.status = AuctionStatus.OPEN;
        totalRevenue = new BigDecimal(0);
        totalQuantitySold = 0;
        this.closedAt = null;
    }

    public Auction() {
    }

    public int getAuctionId() {
        return auctionId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getMinOfferPrice() {
        return minOfferPrice;
    }

    public String getProduct() {
        return product;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public int getTotalQuantitySold() {
        return totalQuantitySold;
    }

    public void close(final List<Bid> bids) {
        bids.stream().sorted(Comparator.reverseOrder()).forEach(bid -> {
            if (totalQuantitySold < quantity) {
                int fillQuantity = bid.getQuantity();
                totalQuantitySold += fillQuantity;

                if (totalQuantitySold > quantity) {
                    fillQuantity = bid.getQuantity() - (totalQuantitySold - quantity);
                    totalQuantitySold = quantity;
                }
                bid.fillBid(fillQuantity);
                bid.setUpdatedAt(Instant.now()); // TODO: Time Provider

                totalRevenue =
                        totalRevenue.add(
                                BigDecimal.valueOf(bid.getOfferPrice()).multiply(BigDecimal.valueOf(fillQuantity)));

            } else {
                bid.fillBid(0);
                bid.setUpdatedAt(Instant.now()); // TODO: Time Provider
            }
        });

        this.status = AuctionStatus.CLOSED;
        closedAt = Instant.now(); // TODO: Time Provider
    }

    public boolean canPlaceBid(final int bidderId, final double offerPrice, final int quantity) {
        return status != AuctionStatus.CLOSED && offerPrice > minOfferPrice && bidderId != ownerId && quantity > 0;
    }

    public Instant getClosedAt() {
        return closedAt;
    }
}
