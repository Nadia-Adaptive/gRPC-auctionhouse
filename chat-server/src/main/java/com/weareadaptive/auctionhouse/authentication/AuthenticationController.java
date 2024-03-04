package com.weareadaptive.auctionhouse.authentication;

import chatapp.ConnectionService.ConnectionServiceOuterClass.ConnectionRequest;
import chatapp.ConnectionService.ConnectionServiceOuterClass.ConnectionResponse;
import com.weareadaptive.auctionhouse.observability.ApplicationLogger;
import com.weareadaptive.auctionhouse.utils.DTOMappers;
import io.grpc.Status;
import reactor.core.publisher.Mono;

import static com.weareadaptive.auctionhouse.configuration.ApplicationContext.getApplicationContext;
import static com.weareadaptive.auctionhouse.model.ResponseStatus.BAD_CREDENTIALS;
import static com.weareadaptive.auctionhouse.observability.ApplicationLogger.getLogger;

public class AuthenticationController {
    AuthenticationService authService;

    public AuthenticationController() {
        this.authService = getApplicationContext().getAuthenticationService();
    }

    public Mono<ConnectionResponse> login(final Mono<ConnectionRequest> request) {

        ApplicationLogger.info("User signed in.");

        return request.log(getLogger()).handle((r, sink) -> {
            final var user = authService.validateUserCredentials(DTOMappers.mapToAuthRequest(r));
            if (user == null) {
                sink.error(Status.INVALID_ARGUMENT.withDescription(BAD_CREDENTIALS).asRuntimeException());
            }
            final var token = authService.generateJWTToken(user.getUsername());

            //.body(new Response<>(new AuthResponse(request.get("username"), UserRole.valueOf(role), token)));
        });
    }
}
