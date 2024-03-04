package com.weareadaptive.auctionhouse;

import com.weareadaptive.auctionhouse.auction.Auction;
import com.weareadaptive.auctionhouse.organisation.Organisation;
import com.weareadaptive.auctionhouse.user.User;
import com.weareadaptive.auctionhouse.user.UserRole;

import java.util.HashMap;
import java.util.Map;

public class TestData {
    public static final String ORG_1 = "Org 1";
    public static final String ORG_2 = "Org 2";
    public static final String ADMIN_ORG = "ADMIN";
    public static final String ORG_404 = "invalid";
    public static final int ADMIN_ID = 1;
    public static final int UID_1 = 2;
    public static final int UID_2 = 3;
    public static final int UID_404 = -3;
    public static final Organisation ORGANISATION1 = new Organisation(0, ORG_1);
    public static final Organisation ORGANISATION2 = new Organisation(0, ORG_2);
    public static final User ADMIN =
            new User(ADMIN_ID, "admin", "admin", "admin", "admin", "ADMIN", UserRole.ADMIN); // TODO: Time provider
    public static final User USER1 =
            new User(UID_1, "testuser1", "password", "john", "doe", ORG_1, UserRole.USER);
    // TODO: Time provider
    public static final User USER2 =
            new User(UID_2, "testuser2", "password", "john", "smith", ORG_1, UserRole.USER);
    // TODO: Time provider

    public static final Auction AUCTION1 = new Auction(ADMIN_ID, UID_1, "TEST", 1.0, 10);
    public static final Auction AUCTION2 = new Auction(ADMIN_ID, UID_2, "TEST2", 2.0, 11);
    public static final String PASSWORD = "mypassword";

    public static Map<String, String> generateJSON(final String... args) {
        final var map = new HashMap<String, String>();
        for (int i = 0; i < args.length; i += 2) {
            try {
                map.put(args[i], args[i + 1]);
            } catch (final IndexOutOfBoundsException ex) {
                return map;
            }
        }
        return map;
    }
}
