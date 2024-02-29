package com.weareadaptive.auction.auction;

import com.weareadaptive.auction.bid.Bid;
import com.weareadaptive.auction.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import static com.weareadaptive.auction.utils.StringUtil.isNullOrEmpty;

@Entity
@Table(name = "auctions")
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auction_id")
    private int id;

    @Column(length = 50)
    private String product;

    @Column(columnDefinition = "numeric")
    private double minPrice;
    private int quantity;
    private int ownerId;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status;
    private Instant closedAt;
    private Instant createdAt;
    private Instant updatedAt;

    @Column(columnDefinition = "varchar")
    private BigDecimal totalRevenue;
    private int totalQuantitySold;

    public Auction(final int ownerId, final String symbol, final double minPrice, final int quantity,
                   final Instant createdAt) {
        if (createdAt == null) {
            throw new BusinessException("CreatedAt cannot be null!");
        }
        if (isNullOrEmpty(symbol)) {
            throw new BusinessException("Symbol cannot be empty!");
        }

        if (minPrice <= 0d) {
            throw new BusinessException("Price cannot be less than or equal to zero.");
        }
        if (quantity <= 0) {
            throw new BusinessException("Quantity cannot be less than or equal to zero.");
        }

        this.ownerId = ownerId;
        this.product = symbol;
        this.minPrice = minPrice;
        this.quantity = quantity;
        this.status = AuctionStatus.OPEN;
        totalRevenue = new BigDecimal(0);
        totalQuantitySold = 0;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.closedAt = null;
    }

    public Auction() {
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getMinPrice() {
        return minPrice;
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
        return status != AuctionStatus.CLOSED && offerPrice > minPrice && bidderId != ownerId && quantity > 0;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
