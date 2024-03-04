package com.weareadaptive.auctionhouse.bid;

import com.weareadaptive.auctionhouse.model.Repository;

import java.util.List;

public class BidRepository extends Repository<Bid> {

    public List<Bid> findByAuction(final int id) {
        return findAll().stream().filter(b -> b.getId() == id)
                .toList(); //   @Query("SELECT b FROM Bid b WHERE b.id = :id")
    }
}
