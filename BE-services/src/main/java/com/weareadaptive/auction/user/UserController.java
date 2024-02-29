package com.weareadaptive.auction.user;

import com.weareadaptive.auction.authentication.AuthenticationController;
import com.weareadaptive.auction.response.Response;
import com.weareadaptive.auction.response.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserService userService;
    Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Response> getAllUsers() {
        logger.info("All users requested.");
        final var users = userService.getUsers();

        return ResponseBuilder.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getUser(@PathVariable final int id) {
        logger.info("User with id " + id + " requested.");

        return ResponseBuilder.ok(userService.getUser(id));
    }

    @PostMapping
    public ResponseEntity<Response> createUser(@RequestBody final UserCreateRequest body) {
        logger.info("Request to create userRole with role " + body.userRole().name());

        final var user = userService.createUser(body);
        return ResponseBuilder.created(user.getId(), user);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity updateUser(@PathVariable final int id, @RequestBody final UserUpdateRequest body) {
        logger.info("Request to update userRole with id " + id + ".");

        return ResponseBuilder.ok(userService.updateUser(id, body));
    }

    @PutMapping(value = "/{id}/status")
    public ResponseEntity updateUserAccessStatus(@PathVariable final int id,
                                                 @RequestBody final UserAccessRequest body) {
        logger.info("Request to update userRole with id " + id + " permissions to " + body.status().name());

        return ResponseBuilder.ok(userService.updateUserStatus(id, body));
    }

    @GetMapping("/auctions")
    ResponseEntity<Response> getAuctions() {
        final var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("Request to get userRole with id " + user.getId() + " auctions.");
        return ResponseBuilder.ok(userService.getUserAuctions(user.getId()));
    }
}
