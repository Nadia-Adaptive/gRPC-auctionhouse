package com.weareadaptive.auctionhouse.server;

import com.weareadaptive.auctionhouse.authentication.AuthenticationService;
import com.weareadaptive.auctionhouse.exception.BadCredentialsException;
import com.weareadaptive.auctionhouse.user.User;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

import static com.weareadaptive.auctionhouse.configuration.ApplicationContext.getApplicationContext;
import static com.weareadaptive.auctionhouse.server.RequestHeader.AUTHORIZATION_KEY;
import static com.weareadaptive.auctionhouse.server.RequestHeader.AUTHORIZATION_TOKEN;

public class HeaderInterceptor implements ServerInterceptor {
    private final AuthenticationService authenticationService;

    public HeaderInterceptor() {
        authenticationService = getApplicationContext().getAuthenticationService();
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call,
                                                                 final Metadata requestHeaders,
                                                                 final ServerCallHandler<ReqT, RespT> next) {
        boolean cancelCall = false;
        try {
            final var user = authenticationUser(requestHeaders);
            var context = Context.current().withValue(RequestHeader.CTX_CLIENT_ID, user.getUserId());
            return Contexts.interceptCall(context, call, requestHeaders, next);
        } catch (final NullPointerException | BadCredentialsException e) {
            cancelCall = true;
            return new ServerCall.Listener<>() {
            };
        } finally {
            if (cancelCall) {
                call.close(Status.UNAUTHENTICATED.withDescription("Invalid auth credentials"), requestHeaders);
            }
        }
    }

    private User authenticationUser(final Metadata headers) {
        String token = headers.get(AUTHORIZATION_KEY);

        if (token == null || !token.startsWith(AUTHORIZATION_TOKEN)) {
            throw new BadCredentialsException("Invalid Credentials");
        }
        token = token.substring(AUTHORIZATION_TOKEN.length());
        System.out.println(token);

        return authenticationService.verifyJWTToken(token);
    }
}
