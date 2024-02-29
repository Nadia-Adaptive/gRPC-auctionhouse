package com.weareadaptive.auctionhouse.utils;

import com.weareadaptive.auctionhouse.model.AccessStatus;
import com.weareadaptive.auctionhouse.model.OrganisationDetails;
import com.weareadaptive.auctionhouse.model.User;

public final class StringUtil {
    private StringUtil() {
    }

    public static boolean isNullOrEmpty(final String theString) {
        return theString == null || theString.isBlank();
    }

    public static String userToString(final User user) {
        return "Username: %s, First name: %s, Last name: %s, Organisation: %s, Has access: %s%n".formatted(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getOrganisation(),
                user.getAccessStatus() == AccessStatus.ALLOWED ? "Yes" : "No");
    }

    public static String organisationToString(final OrganisationDetails organisation) {
        return "Organisation: %s%n%s".formatted(
                organisation.organisationName(),
                organisation.users().stream()
                        .map(u -> "Username: %s".formatted(u.getUsername()).indent(2))
                        .sorted()
                        .reduce((String acc, String val) -> String.join("\n", acc, val)).orElse("")
        );
    }
}
