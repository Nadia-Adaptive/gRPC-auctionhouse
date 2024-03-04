package com.weareadaptive.auctionhouse.auction;

import com.weareadaptive.auctionhouse.IntegrationTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

import static com.weareadaptive.auctionhouse.TestData.generateJSON;
import static org.junit.Assert.fail;

class AuctionGRPCControllerTest {
    AuctionService service;

    IntegrationTestData apiData;

    AuctionGRPCControllerTest() {

    }

    private static Map<String, String> generateInput(final String product, final String minPrice,
                                                     final String quantity) {
        return generateJSON("product", product, "minPrice", minPrice, "quantity", quantity);
    }

    private static Stream<Map<String, String>> invalidInput() {
        return Stream.of(
                generateInput("", "1.0", "10"),
                generateInput("TEST", "", "10"),
                generateInput("TEST", "1.0", ""));
    }

    @Test
    @DisplayName("when POST /auctions is provided valid json returns a message and 201 http code")
    public void postAuction() {
        fail();
    }

    @DisplayName("when createAuctions is provided invalid input, it returns errors")
    public void postAuctionWithInvalidInput(final Map<String, String> input) {
        fail();
    }

    @Test
    @DisplayName("when GET /auctions is provided a valid id, it returns the auction")
    public void getAuction() {
        fail();
    }

    @Test
    @DisplayName("when GET /auctions is provided an invalid id, it returns 404 and a message")
    public void getAuctionInvalidId() {
        fail();
    }

    @Test
    @DisplayName("when GET /auctions returns a list of auctions excluding the requester's")
    public void getAllAuctions() {
        fail();
    }

    @Test
    @DisplayName("when GET /auctions/available returns a list of open auctions belonging to the requester")
    public void getAvailableAuctions() {
        fail();
    }

    @Test
    @DisplayName("when PUT /auctions/{id}/bids make a bid on the auction matching the id")
    public void putAuctionMakeABid() {
        fail();
    }

    @Test
    @DisplayName("when PUT /{id}/close close the auction matching the id")
    public void putCloseAuction() {
        fail();
    }

    @Test
    @DisplayName("when PUT /{id}/close and auction doesn't exist returns 404 and a message")
    public void putCloseAuctionInvalidAuction() {
        fail();
    }

    @Test
    @DisplayName("when PUT /{id}/close and userRole doesn't own resource returns 403 and a message")
    public void putCloseAuctionUserDoesntOwnAuction() {
        fail();
    }
}
