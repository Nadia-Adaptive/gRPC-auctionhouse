package com.weareadaptive.auction;

import com.weareadaptive.auction.auction.Auction;
import com.weareadaptive.auction.organisation.Organisation;
import com.weareadaptive.auction.user.User;
import com.weareadaptive.auction.user.UserRole;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class TestData {
    public static final String ORG_1 = "Org 1";
    public static final String ORG_2 = "Org 2";
    public static final String ADMIN_ORG = "ADMIN";
    public static final String ORG_404 = "invalid";
    public static final Organisation ORGANISATION1 = new Organisation(ORG_1);
    public static final Organisation ORGANISATION2 = new Organisation(ORG_2);

    public static final User ADMIN =
            new User("admin", "admin", "admin", "admin", "ADMIN", UserRole.ADMIN, Instant.now()); // TODO: Time provider
    public static final User USER1 =
            new User("testuser1", "password", "john", "doe", ORG_1, UserRole.USER, Instant.now());
            // TODO: Time provider
    public static final User USER2 =
            new User("testuser2", "password", "john", "smith", ORG_1, UserRole.USER, Instant.now());
            // TODO: Time provider

    public static final int AID = 1;
    public static final int UID_1 = 2;
    public static final int UID_2 = 3;
    public static final int UID_404 = -3;
    public static final Auction AUCTION1 = new Auction(UID_1, "TEST", 1.0, 10, Instant.now());
    public static final Auction AUCTION2 = new Auction(UID_2, "TEST2", 2.0, 11, Instant.now());
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
