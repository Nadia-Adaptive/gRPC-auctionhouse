package com.weareadaptive.auction.authentication;

import com.weareadaptive.auction.user.User;
import com.weareadaptive.auction.user.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class AuthenticationService {
    SecretKey key;
    UserRepository repo;

    public AuthenticationService(final UserRepository repo) {
        key = Jwts.SIG.HS256.key().build();
        this.repo = repo;
    }

    public User validateUserCredentials(final String username, final String password) {
        final User user = repo.findByUsername(username);

        if (user != null && user.validatePassword(password)) {
            return user;
        }
        return null;
    }

    public String generateJWTToken(final String username) {
        final var user = repo.findByUsername(username);
        if (user == null) {
            throw new BadCredentialsException("Invalid userRole.");
        }
        return Jwts.builder().subject(username).claim("role", user.getUserRole().name()).claim("id", user.getId())
                .signWith(key).compact();
    }

    public User verifyJWTToken(final String token) {
        try {
            final var parsedToken = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return repo.findById(parsedToken.getPayload().get("id", Integer.class)).orElse(null);
        } catch (final JwtException exception) {
            throw new BadCredentialsException("Invalid token.");
        }
    }
}
