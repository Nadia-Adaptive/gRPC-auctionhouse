package com.weareadaptive.auction.utils;

public final class StringUtil {
    private StringUtil() {
    }
    public static boolean isNullOrEmpty(final String theString) {
        return theString == null || theString.isBlank();
    }
}
