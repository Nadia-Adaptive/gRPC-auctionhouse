package com.weareadaptive.auction.security;

import com.weareadaptive.auction.authentication.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.ArrayList;

import static com.weareadaptive.auction.authentication.AuthHeaders.AUTHORIZATION_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    @Autowired
    private AuthenticationService authService;

    public AuthenticationFilter(final RequestMatcher requiresAuth) {
        super(requiresAuth);
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest httpServletRequest,
                                                final HttpServletResponse httpServletResponse)
            throws AuthenticationException {
        String token = httpServletRequest.getHeader(AUTHORIZATION);

        if (token == null || !token.startsWith(AUTHORIZATION_TOKEN)) {
            throw new BadCredentialsException("Invalid Credentials");
        }
        token = token.substring(AUTHORIZATION_TOKEN.length());
        System.out.println(token);

        final var user = authService.verifyJWTToken(token);
        if (user == null) {
            throw new BadCredentialsException("User doesn't exist in db");
        }
        return new UsernamePasswordAuthenticationToken(user, token, new ArrayList<>() {{
            add(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()));
        }});
    }

    @Override
    protected void successfulAuthentication(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain chain,
            final Authentication authResult) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }
}
