package com.weareadaptive.auctionhouse.businesslogic.model;

import java.time.Instant;

public class InstantTimeProvider implements TimeProvider {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
