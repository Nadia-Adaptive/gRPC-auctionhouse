package com.weareadaptive.auctionhouse.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserRepositoryTest {
    private UserRepository userRepository;
    @Test
    @DisplayName("findUserByUsername should return a userRole when passed valid credentials")
    public void shouldGetUserWhenPassedValidCredentials() {
        final var user = userRepository.findByUsername("ADMIN");
        assertEquals("ADMIN", user.getUsername());
        assertEquals(UserRole.ADMIN, user.getUserRole());
    }

    @Test
    @DisplayName("findUserByUsername should return null when passed invalid credentials")
    public void getUserByUsernamePassedInvalidUsername() {
        final var user = userRepository.findByUsername("notValid");
        assertEquals(null, user);
    }
}
