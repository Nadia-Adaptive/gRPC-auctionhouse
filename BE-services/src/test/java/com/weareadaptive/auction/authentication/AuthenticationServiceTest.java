package com.weareadaptive.auction.authentication;

import com.weareadaptive.auction.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

import static com.weareadaptive.auction.TestData.USER1;
import static com.weareadaptive.auction.TestData.USER2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationServiceTest {
    private AuthenticationService service;
    private final String password = "password";
    UserRepository repo;

    public AuthenticationServiceTest() {
        repo = mock(UserRepository.class);
        when(repo.findByUsername(USER1.getUsername())).thenReturn(USER1);
        when(repo.findById(USER1.getId())).thenReturn(Optional.of(USER1));
    }

    @BeforeEach
    void beforeEach() {
        service = new AuthenticationService(repo);
    }

    @Test
    @DisplayName("GenerateJWTToken provided valid User returns a token")
    void successfulGenerateJWTToken() {
        final String token = service.generateJWTToken(USER1.getUsername());
        assertTrue(token.contains("eyJhbGciOiJIUzI1NiJ9."));
    }

    @Test
    @DisplayName("GenerateJWTToken provided invalid User throws an exception")
    void failedGenerateJWTToken() {
        assertThrows(BadCredentialsException.class, () -> service.generateJWTToken(USER2.getUsername()));
    }

    @Test
    @DisplayName("verifyJWTToken provided valid token returns userRole")
    void successfulVerifyJWTToken() {
        final var user = service.verifyJWTToken(service.generateJWTToken(USER1.getUsername()));
        assertEquals(USER1,  user);
    }

    @Test
    @DisplayName("verifyJWTToken provided invalid token throws an exception")
    void failedVerifyJWTToken() {
        assertThrows(BadCredentialsException.class, () -> service.verifyJWTToken("invalid"));
    }

    @Test
    @DisplayName("validateUserCredentials should return a userRole when passed valid credentials")
    public void validateUserCredentials() {
        assertEquals(USER1, service.validateUserCredentials(USER1.getUsername(), password));
    }

    @Test
    @DisplayName("validateUserCredentials should return null when passed invalid credentials")
    public void validateUserCredentialsProvidedInvalidToken() {
        assertNull(service.validateUserCredentials("invalid", password));
    }
}
