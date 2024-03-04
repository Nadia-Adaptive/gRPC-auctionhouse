package com.weareadaptive.auctionhouse.authentication;

import com.weareadaptive.auctionhouse.user.User;
import com.weareadaptive.auctionhouse.user.UserRepository;
import com.weareadaptive.auctionhouse.exception.BadCredentialsException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

public class AuthenticationService {
    SecretKey key;
    UserRepository repo;

    public AuthenticationService(final UserRepository repo) {
        key = Jwts.SIG.HS256.key().build(); // TODO: Read from file
        this.repo = repo;
    }

    public User validateUserCredentials(final AuthenticationRequest request) {
        final User user = repo.findByUsername(request.username());

        if (user != null && user.validatePassword(request.password())) {
            return user;
        }
        return null;
    }

    public String generateJWTToken(final String username) {
        final var user = repo.findByUsername(username);
        if (user == null) {
            throw new BadCredentialsException("Invalid userRole.");
        }
        return Jwts.builder().subject(username).claim("role", user.getUserRole().name()).claim("id", user.getUserId())
                .signWith(key).compact();
    }

    public User verifyJWTToken(final String token) {
        try {
            final var parsedToken = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return repo.findById(parsedToken.getPayload().get("id", Integer.class));
        } catch (final JwtException exception) {
            throw new BadCredentialsException("Invalid token.");
        }
    }
}
