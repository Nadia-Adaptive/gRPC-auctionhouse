package com.weareadaptive.auction.model;

import java.time.Instant;

public interface TimeProvider {
    Instant now();
}
