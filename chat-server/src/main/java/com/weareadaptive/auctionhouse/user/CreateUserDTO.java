package com.weareadaptive.auctionhouse.user;

public record CreateUserDTO(String username, String password, String firstName, String lastName,
                            String organisationName, UserRole userRole) {
}
