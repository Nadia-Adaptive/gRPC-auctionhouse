package com.weareadaptive.auctionhouse.model;

import java.time.Instant;
import java.util.Objects;

public class Bid implements Comparable<Bid> {
    private final Instant timestamp;
    private final double price;
    private final int quantity;
    private final User buyer;
    private int quantityFilled;
    private BidFillStatus status;

    public Bid(final User buyer, final double price, final int quantity, final Instant timestamp) {
        if (buyer == null) {
            throw new BusinessException("Buyer cannot be null.");
        }
        if (price <= 0) {
            throw new BusinessException("Price must be greater than zero.");
        }
        if (quantity <= 0) {
            throw new BusinessException("Quantity must be greater than zero.");
        }

        this.price = price;
        this.quantity = quantity;
        this.buyer = buyer;
        this.status = BidFillStatus.PENDING;
        this.timestamp = timestamp;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public User getBuyer() {
        return buyer;
    }

    @Override
    public int compareTo(final Bid o) {
        if (price == o.price && timestamp.equals(o.timestamp)) {
            return 0;
        }

        if (this.price > o.price) {
            return 1;
        }

        if (this.price == o.price && o.timestamp.isAfter(this.timestamp)) {
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
        return getTimestamp().equals(bid.getTimestamp()) && getBuyer().equals(bid.getBuyer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTimestamp(), getBuyer());
    }
}
