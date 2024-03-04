package com.weareadaptive.auctionhouse.user;

import com.google.protobuf.Empty;
import com.weareadaptive.auctionhouse.configuration.ApplicationContext;
import com.weareadaptive.auctionhouse.exception.BusinessException;
import com.weareadaptive.auctionhouse.exception.InternalErrorException;
import com.weareadaptive.auctionhouse.exception.NotFoundException;
import com.weareadaptive.auctionhouse.organisation.OrganisationService;
import com.weareadaptive.auctionhouse.user.ReactorUserServiceGrpc.UserServiceImplBase;
import com.weareadaptive.auctionhouse.user.gRPCUserService.CreateUserRequest;
import com.weareadaptive.auctionhouse.user.gRPCUserService.GetUserRequest;
import com.weareadaptive.auctionhouse.user.gRPCUserService.UpdateUserAccessRequest;
import com.weareadaptive.auctionhouse.user.gRPCUserService.UpdateUserRequest;
import com.weareadaptive.auctionhouse.user.gRPCUserService.UserResponse;
import com.weareadaptive.auctionhouse.user.gRPCUserService.UsersResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;

import static com.weareadaptive.auctionhouse.observability.ApplicationLogger.info;
import static com.weareadaptive.auctionhouse.user.UserMapper.mapToUserResponse;
import static com.weareadaptive.auctionhouse.utils.DTOMappers.mapToGRPCError;
import static io.grpc.Status.INTERNAL;
import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static io.grpc.Status.UNAUTHENTICATED;

public class UserGRPCController extends UserServiceImplBase {
    private final UserService userService;
    private final OrganisationService organisationService;
    private final Sinks.Many<UsersResponse> userSink = Sinks.many().multicast().onBackpressureBuffer();

    public UserGRPCController() {
        this.userService = ApplicationContext.getApplicationContext().getUserService();
        this.organisationService = ApplicationContext.getApplicationContext().getOrganisationService();
    }

    @Override
    public Flux<UsersResponse> subscribeToGetUsersService(final Flux<Empty> request) {
        info("All users requested.");

        return userSink.asFlux();
    }

    @Override
    public Mono<UserResponse> getUser(final Mono<GetUserRequest> request) {
        return request.handle((r, sink) -> {
            info("User with id " + r.getUserId() + " requested.");

            try {
                sink.next(mapToUserResponse(userService.getUser(r.getUserId())));
            } catch (final NotFoundException e) {
                sink.error(mapToGRPCError(NOT_FOUND, e));
            }
        });
    }

    @Override
    public Mono<UserResponse> createUser(final Mono<CreateUserRequest> request) {
        return request
                .map(UserMapper::mapToCreateUserDTO
                ).handle((r, sink) -> {
                    info("Request to create userRole with role " + r.userRole());
                    try {
                        try {
                            organisationService.getOrganisation(r.organisationName());
                        } catch (final NotFoundException e) {
                            organisationService.addOrganisation(r.organisationName());
                        }

                        final var user = userService.createUser((r));

                        userSink.tryEmitNext(UserMapper.mapToGetResponses(List.of(user), false));

                        sink.next(mapToUserResponse(user));
                    } catch (final InternalErrorException | IndexOutOfBoundsException e) {
                        sink.error(mapToGRPCError(INTERNAL, e));
                    } catch (final NullPointerException e) {
                        sink.error(mapToGRPCError(UNAUTHENTICATED, e));
                    } catch (final BusinessException e) {
                        sink.error(mapToGRPCError(INVALID_ARGUMENT, e));
                    }
                });
    }

    @Override
    public Mono<UserResponse> updateUser(final Mono<UpdateUserRequest> request) {
        return request.map(UserMapper::mapToUpdateUserDTO).handle((r, sink) -> {
            info("Request to update userRole with id " + r.userId() + ".");
            try {
                sink.next(mapToUserResponse(userService.updateUser(r)));
            } catch (final NotFoundException e) {
                sink.error(mapToGRPCError(NOT_FOUND, e));
            } catch (final InternalErrorException e) {
                sink.error(mapToGRPCError(INTERNAL, e));
            } catch (final NullPointerException e) {
                sink.error(mapToGRPCError(UNAUTHENTICATED, e));
            }
        });
    }

    @Override
    public Mono<UserResponse> updateUserAccess(final Mono<UpdateUserAccessRequest> request) {
        return request.map(UserMapper::mapToUserAccessDTO).handle((r, sink) -> {
            try {
                info("Request to update userRole with id " + r.userId() + " permissions to " + r.status().name());

                //userSink.
                sink.next(mapToUserResponse(userService.updateUserStatus(r)));
            } catch (final NotFoundException e) {
                sink.error(mapToGRPCError(NOT_FOUND, e));
            } catch (final InternalErrorException e) {
                sink.error(mapToGRPCError(INTERNAL, e));
            } catch (final NullPointerException e) {
                sink.error(mapToGRPCError(UNAUTHENTICATED, e));
            }
        });
    }
}
