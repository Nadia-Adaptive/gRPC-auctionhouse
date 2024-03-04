package com.weareadaptive.auctionhouse.utils;

import chatapp.ConnectionService.ConnectionServiceOuterClass.ConnectionRequest;
import chatapp.ConnectionService.ConnectionServiceOuterClass.ConnectionResponse;
import com.weareadaptive.auctionhouse.authentication.AuthenticationRequest;
import io.grpc.Status;

public class DTOMappers {
    public static AuthenticationRequest mapToAuthRequest(final ConnectionRequest request) {
        return new AuthenticationRequest(request.getUsername(), "password");
    }

    public static ConnectionResponse mapToConnectionResponse(final int id, final String token) {
        return ConnectionResponse.newBuilder().setClientId(id).build();
    }

    public static Throwable mapToGRPCError(final Status status, final Throwable e) {
        return status.withDescription(e.getMessage()).asRuntimeException();
    }

}
