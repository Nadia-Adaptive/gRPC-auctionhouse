package com.weareadaptive.auction.bid;

import org.junit.jupiter.api.AfterAll;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BidRepositoryTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    private String uri;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Autowired
    BidRepository repository;

    @BeforeEach
    void beforeEach() {
        repository.save(new Bid(1, 2, 1.2, 10, Instant.now())); // TODO: Time provider
    }

    @Test
    @DisplayName("findByAuction returns a bid")
    public void findByAuction() {
        assertEquals(1, repository.findByAuction(1).size());
    }

    @Test
    @DisplayName("findByAuction returns nothing if bid doesn't exist")
    public void findByAuctionDoesNotExist() {
        assertEquals(0, repository.findByAuction(2).size());
    }
}