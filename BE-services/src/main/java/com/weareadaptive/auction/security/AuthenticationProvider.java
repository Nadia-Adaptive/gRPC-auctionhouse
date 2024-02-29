package com.weareadaptive.auction.security;

import com.weareadaptive.auction.authentication.AuthenticationService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public boolean supports(final Class<?> authentication) {
        return super.supports(authentication);
    }

    @Override
    protected void additionalAuthenticationChecks(
            final UserDetails userDetails,
            final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken)
            throws AuthenticationException {
        System.out.println(userDetails);
    }

    @Override
    protected UserDetails retrieveUser(
            final String userName,
            final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken)
            throws AuthenticationException {
        Object principal = usernamePasswordAuthenticationToken.getPrincipal();
        Object password = usernamePasswordAuthenticationToken.getCredentials();
        return Optional
                .ofNullable(principal)
                .map(s -> getUser(String.valueOf(principal), String.valueOf(password)))
                .orElseThrow();
    }

    private UserDetails getUser(@NotNull final String username, @NotNull final String password) {

        var user = authenticationService.validateUserCredentials(username, password);

        if (user == null) {
            throw new UsernameNotFoundException("Bad token");
        }
        return User.builder()
                .username(user.getUsername())
                .password(password)
                .roles(user.getUserRole().toString())
                // .disabled(userRole.get().isBlocked())
                .build();
    }
}
