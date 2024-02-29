package com.weareadaptive.auctionhouse.model;

import java.time.Instant;

public interface TimeProvider {
    Instant now();
}
