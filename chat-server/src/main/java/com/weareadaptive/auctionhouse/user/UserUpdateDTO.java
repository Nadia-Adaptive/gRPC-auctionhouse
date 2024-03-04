package com.weareadaptive.auctionhouse.user;

public record UserUpdateDTO(int userId, String password, String firstName, String lastName, String organisationName) {
}
