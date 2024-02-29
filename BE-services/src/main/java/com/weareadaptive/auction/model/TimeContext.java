package com.weareadaptive.auction.model;

import java.time.Instant;

public class TimeContext {
    private final TimeProvider provider;

    public TimeContext(final TimeProvider provider) {
        this.provider = provider;
    }

    public Instant getNow() {
        return provider.now();
    }
}

