package com.weareadaptive.auction.user;

public record UserUpdateRequest(String password, String firstName, String lastName, String organisationName,
                                UserRole userRole) {
}
