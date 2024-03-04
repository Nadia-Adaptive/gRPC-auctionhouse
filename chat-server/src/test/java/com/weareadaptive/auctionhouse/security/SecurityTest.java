package com.weareadaptive.auctionhouse.security;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.fail;

public class SecurityTest {
    @Test
    public void shouldBeUnauthorizedWhenNotAuthenticated() {
       fail();
    }

    @Test
    public void shouldBeAuthenticated() {
       fail();
    }

    @Test
    public void shouldBeAnAdmin() {
        fail();
    }

    @Test
    public void shouldReturnForbiddenWhenNotAnAdmin() {
        fail();
    }

    @Test
    public void shouldReturnUnauthorizedWhenProvidedInvalidToken() {
        fail();
    }
}
