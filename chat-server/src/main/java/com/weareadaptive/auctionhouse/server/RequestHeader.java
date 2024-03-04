package com.weareadaptive.auctionhouse.server;

import io.grpc.Context;
import io.grpc.Metadata;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

public class RequestHeader {
    public static final String REFRESH_TOKEN = "refresh-token";
    public static final String AUTHORIZATION_TOKEN = "Bearer ";
    public static final Metadata.Key<String> CLIENT_ID = Metadata.Key.of("client_id", Metadata.ASCII_STRING_MARSHALLER);
    public static final Context.Key<Integer> CTX_CLIENT_ID = Context.key("client_id");

    public static final Metadata.Key<String> ROOM_ID = Metadata.Key.of("room_id", Metadata.ASCII_STRING_MARSHALLER);
    public static final Context.Key<Integer> CTX_ROOM_ID = Context.key("room_id");

    public static final Metadata.Key<String> AUTHORIZATION_KEY =
            Metadata.Key.of(AUTHORIZATION, Metadata.ASCII_STRING_MARSHALLER);
    public static final Context.Key<Integer> CTX_AUTHORIZATION_KEY = Context.key(AUTHORIZATION);
}
