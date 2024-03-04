package com.weareadaptive.auctionhouse.auction;

import com.weareadaptive.auctionhouse.model.Repository;

import java.util.List;

public class AuctionRepository extends Repository<Auction> {
    public List<Auction> findUserAuctions(final int userId) {
        return findAll().stream().filter(a -> a.getOwnerId() == userId).toList();
    }

    List<Auction> findAvailableAuctions(final int userId) {
        return findAll(userId).stream().filter(a -> a.getStatus() == AuctionStatus.OPEN).toList();
    }

    List<Auction> findAll(final int userId) {
        return findAll().stream().filter(a -> a.getOwnerId() != userId).toList();
    }
}
