package com.weareadaptive.auctionhouse.businesslogic.model;

import java.time.Instant;

public interface TimeProvider {
    Instant now();
}
