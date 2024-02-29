package com.weareadaptive.auction.auction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Integer> {

    @Query("SELECT a FROM Auction a WHERE a.ownerId = :userId")
    List<Auction> findUserAuctions(int userId);

    @Query("SELECT a FROM Auction a WHERE a.ownerId != :userId AND a.status = 'OPEN'")
    List<Auction> findAvailableAuctions(int userId);

    @Query("SELECT a FROM Auction a WHERE a.ownerId != :userId")
    List<Auction> findAll(int userId);
}
