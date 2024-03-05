package com.weareadaptive.auctionhouse.user;

import com.weareadaptive.auctionhouse.configuration.ApplicationContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserRepositoryTest {
    private UserRepository userRepository;

    public UserRepositoryTest() {
        ApplicationContext context = new ApplicationContext();
        userRepository = context.getUserRepo();
    }

    @Test
    @DisplayName("findUserByUsername returns a user")
    public void shouldGetUserWhenPassedValidCredentials() {
        final var user = userRepository.findByUsername("ADMIN");
        assertEquals("ADMIN", user.getUsername());
        assertEquals(UserRole.ADMIN, user.getUserRole());
    }

    @Test
    @DisplayName("findUserByUsername returns null when passed invalid credentials")
    public void getUserByUsernamePassedInvalidUsername() {
        final var user = userRepository.findByUsername("notValid");
        assertEquals(null, user);
    }
}
