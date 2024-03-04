package com.weareadaptive.auctionhouse.authentication.connection;

import chatapp.ConnectionService.ConnectionServiceOuterClass.ConnectionRequest;
import chatapp.ConnectionService.ConnectionServiceOuterClass.ConnectionResponse;
import chatapp.ConnectionService.ReactorConnectionServiceGrpc.ConnectionServiceImplBase;
import com.weareadaptive.auctionhouse.authentication.AuthenticationService;
import com.weareadaptive.auctionhouse.configuration.ApplicationContext;
import io.grpc.Status;
import reactor.core.publisher.Mono;

import static com.weareadaptive.auctionhouse.utils.DTOMappers.mapToAuthRequest;
import static com.weareadaptive.auctionhouse.utils.DTOMappers.mapToConnectionResponse;

public class ConnectionServiceImpl extends ConnectionServiceImplBase {
    final AuthenticationService authenticationService;

    public ConnectionServiceImpl() {
        authenticationService = ApplicationContext.getApplicationContext().getAuthenticationService();
    }

    @Override
    public Mono<ConnectionResponse> connect(final Mono<ConnectionRequest> request) {
        return request.handle((r, sink) -> {
            System.out.println("User connection request received.");

            final var user = authenticationService.validateUserCredentials(mapToAuthRequest(r));

            if (user == null) {
                sink.error(Status.INVALID_ARGUMENT.withDescription("Invalid credentials").asRuntimeException());
            }

            final var userId = user.getUserId();
            System.out.println("User connection request completed.");
            sink.next(mapToConnectionResponse(userId, "token here"));
        });
    }
}
