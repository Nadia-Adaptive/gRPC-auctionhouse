package com.weareadaptive.auction.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public record UserDTO(int id, String username, String password, String firstName, String lastName,
                      String organisationName, UserRole userRole) {
}

// TODO: Rename DTOs and separate the concerns (create vs update)