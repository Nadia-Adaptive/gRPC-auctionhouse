package com.weareadaptive.auctionhouse.authentication;

import com.weareadaptive.auctionhouse.TestData;
import com.weareadaptive.auctionhouse.exception.BadCredentialsException;
import com.weareadaptive.auctionhouse.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        when(repo.findByUsername(TestData.USER1.getUsername())).thenReturn(TestData.USER1);
        when(repo.findById(TestData.USER1.getUserId())).thenReturn(TestData.USER1);
    }

    @BeforeEach
    void beforeEach() {
        service = new AuthenticationService(repo);
    }

    @Test
    @DisplayName("GenerateJWTToken provided valid User returns a token")
    void successfulGenerateJWTToken() {
        final String token = service.generateJWTToken(TestData.USER1.getUsername());
        assertTrue(token.contains("eyJhbGciOiJIUzI1NiJ9."));
    }

    @Test
    @DisplayName("GenerateJWTToken provided invalid User throws an exception")
    void failedGenerateJWTToken() {
        assertThrows(BadCredentialsException.class, () -> service.generateJWTToken(TestData.USER2.getUsername()));
    }

    @Test
    @DisplayName("verifyJWTToken provided valid token returns userRole")
    void successfulVerifyJWTToken() {
        final var user = service.verifyJWTToken(service.generateJWTToken(TestData.USER1.getUsername()));
        Assertions.assertEquals(TestData.USER1, user);
    }

    @Test
    @DisplayName("verifyJWTToken provided invalid token throws an exception")
    void failedVerifyJWTToken() {
        assertThrows(BadCredentialsException.class, () -> service.verifyJWTToken("invalid"));
    }

    @Test
    @DisplayName("validateUserCredentials should return a userRole when passed valid credentials")
    public void validateUserCredentials() {
        Assertions.assertEquals(TestData.USER1,
                service.validateUserCredentials(new AuthenticationRequest(TestData.USER1.getUsername(), password)));
    }

    @Test
    @DisplayName("validateUserCredentials should return null when passed invalid credentials")
    public void validateUserCredentialsProvidedInvalidToken() {
        assertNull(service.validateUserCredentials(new AuthenticationRequest("invalid", password)));
    }
}
