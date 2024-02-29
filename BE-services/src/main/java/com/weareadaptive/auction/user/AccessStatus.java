package com.weareadaptive.auction.user;

public enum AccessStatus {
    BLOCKED("BLOCKED"),
    ALLOWED("ALLOWED");

    private final String status;

    AccessStatus(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
