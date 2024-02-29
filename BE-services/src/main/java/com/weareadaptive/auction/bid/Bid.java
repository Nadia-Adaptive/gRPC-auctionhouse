package com.weareadaptive.auction.bid;

import com.weareadaptive.auction.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;


@Entity
@Table(name = "bids")
public class Bid implements Comparable<Bid> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_id")
    private int id;
    private int auctionId;
    private int bidderId;
    @Column(columnDefinition = "numeric")
    private double offerPrice;
    private int quantity;
    private int quantityFilled;
    private Instant createdAt;
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private BidFillStatus status;

    public Bid(final int auctionId, final int bidderId, final double price, final int quantity,
               final Instant timestamp) {
        if (price <= 0) {
            throw new BusinessException("Price must be greater than zero.");
        }
        if (quantity <= 0) {
            throw new BusinessException("Quantity must be greater than zero.");
        }

        this.auctionId = auctionId;
        this.offerPrice = price;
        this.quantity = quantity;
        this.bidderId = bidderId;
        this.status = BidFillStatus.PENDING;
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
    }

    public Bid() {

    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public double getOfferPrice() {
        return offerPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getBidderId() {
        return bidderId;
    }

    @Override
    public int compareTo(final Bid o) {
        if (offerPrice == o.offerPrice && createdAt.equals(o.createdAt)) {
            return 0;
        }

        if (this.offerPrice > o.offerPrice) {
            return 1;
        }

        if (this.offerPrice == o.offerPrice && o.createdAt.isAfter(this.createdAt)) {
            return 1;
        }
        return -1;
    }

    public BidFillStatus getStatus() {
        return status;
    }

    public int getQuantityFilled() {
        return quantityFilled;
    }

    public void fillBid(final int quantityFilled) {
        if (quantityFilled < 0) {
            throw new BusinessException("Cannot fill a bid with a negative number");
        }
        if (quantityFilled > quantity) {
            throw new BusinessException("Cannot fill a bid with a greater quantity than offered");
        }
        if (status != BidFillStatus.PENDING) {
            throw new BusinessException("Cannot fill a closed bid");
        }

        this.quantityFilled = quantityFilled;
        if (quantityFilled == 0) {
            this.status = BidFillStatus.UNFILLED;
        } else if (quantityFilled < quantity) {
            this.status = BidFillStatus.PARTIALFILL;
        } else if (quantityFilled == quantity) {
            this.status = BidFillStatus.FILLED;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Bid bid = (Bid) o;
        return getCreatedAt().equals(bid.getCreatedAt()) && getBidderId() == bid.getBidderId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCreatedAt(), getBidderId());
    }


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(int auctionId) {
        this.auctionId = auctionId;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
