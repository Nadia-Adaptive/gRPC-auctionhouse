package com.weareadaptive.auction.authentication;

import com.weareadaptive.auction.user.UserRole;

public record AuthResponse(String username, UserRole role, String token) {
}
