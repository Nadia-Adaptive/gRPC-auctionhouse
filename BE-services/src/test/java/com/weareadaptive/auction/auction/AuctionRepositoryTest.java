package com.weareadaptive.auction.auction;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Instant;

import static com.weareadaptive.auction.TestData.UID_1;
import static com.weareadaptive.auction.TestData.UID_2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AuctionRepositoryTest {
    @Autowired
    private AuctionRepository repository;
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static protected void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static protected void afterAll() {
        postgres.stop();
    }


    @BeforeEach
    public void beforeEach() {
        repository.save(new Auction(UID_1, "TEST", 1.0, 10, Instant.now()));
    }

    @AfterEach
    public void afterEach() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("getUserAuctions returns a list of all a userRole's auctions")
    public void getUsersAuctions() {
        final var allAuctions = repository.findUserAuctions(UID_1);

        assertEquals(2, allAuctions.size());
    }

    @Test
    @DisplayName("getAvailableAuctions returns a list of all the auctions a userRole can bid on")
    public void getAvailableAuctions() {
        final var availableAuctions = repository.findAvailableAuctions(UID_2);

        assertEquals(1, availableAuctions.size());
    }

    @Test
    @DisplayName("findAll with userId returns all the auctions excluding the requester's")
    void getAllAuctions() {
        final var auctions = repository.findAll(UID_2);
        assertEquals(1, auctions.size());
        assertTrue(auctions.stream().allMatch(a -> a.getOwnerId() != UID_2));
    }

    @Test
    @DisplayName("findUserAuctions returns all the requester's auctions")
    void getUserAuctions() {
        final var auctions = repository.findUserAuctions(UID_1);
        assertEquals(1, auctions.size());
        assertTrue(auctions.stream().allMatch(a -> a.getOwnerId() == UID_1));
    }
}
