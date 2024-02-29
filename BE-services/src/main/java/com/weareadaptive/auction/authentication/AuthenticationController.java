package com.weareadaptive.auction.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weareadaptive.auction.exception.BusinessException;
import com.weareadaptive.auction.response.Response;
import com.weareadaptive.auction.response.ResponseStatus;
import com.weareadaptive.auction.security.AuthenticationProvider;
import com.weareadaptive.auction.user.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import static com.weareadaptive.auction.authentication.AuthHeaders.REFRESH_TOKEN;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController()
@PreAuthorize("hasRole('ROLE_ANONYMOUS')")
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {
    ObjectMapper mapper;
    Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    AuthenticationProvider authenticationProvider;
    AuthenticationService authService;

    public AuthenticationController(final AuthenticationProvider authenticationProvider, final ObjectMapper mapper,
                                    final AuthenticationService authService) {
        this.authenticationProvider = authenticationProvider;
        this.mapper = mapper;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody final HashMap<String, String> body) {
        Authentication requestAuthentication =
                new UsernamePasswordAuthenticationToken(body.get("username"), body.get("password"));

        final var auth = authenticationProvider.authenticate(requestAuthentication);

        if (!auth.isAuthenticated()) {
            throw new BadCredentialsException(ResponseStatus.BAD_CREDENTIALS);
        }

        final var token = authService.generateJWTToken(body.get("username"));
        SecurityContextHolder.getContext().setAuthentication(auth);

        logger.info("User signed in.");

        final var role = auth.getAuthorities().toArray()[0].toString().substring("ROLE_".length());

        final var cookie =
                ResponseCookie.from(REFRESH_TOKEN, token).path("/").secure(true).httpOnly(true).sameSite("None")
                        .build()
                        .toString();

        return ResponseEntity.ok().header(SET_COOKIE, cookie)
                .body(new Response<>(new AuthResponse(body.get("username"), UserRole.valueOf(role), token)));
    }

    @PostMapping("/token")
    public ResponseEntity<Response> token(@CookieValue(value = REFRESH_TOKEN) String cookie) {
        System.out.println(cookie);

        try {
            final var user = authService.verifyJWTToken(cookie);
            return ResponseEntity.ok().body(new Response<>(
                    new AuthResponse(user.getUsername(), user.getUserRole(), authService.generateJWTToken(
                            user.getUsername()))));

        } catch (final BusinessException ex) {
            final var expiredCookie =
                    ResponseCookie.from(REFRESH_TOKEN, "").path("/").secure(true).httpOnly(true).sameSite("None")
                            .maxAge(0)
                            .build()
                            .toString();
            return ResponseEntity.status(UNAUTHORIZED).header(SET_COOKIE, expiredCookie).build();
        }

    }
}
