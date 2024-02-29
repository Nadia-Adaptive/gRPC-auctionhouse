package com.weareadaptive.auction.user;

public record UserCreateRequest(String username, String password, String firstName, String lastName,
                                String organisationName, UserRole userRole) {
}
