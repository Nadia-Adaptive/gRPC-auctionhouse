package com.weareadaptive.auctionhouse.user;

import com.google.protobuf.Empty;
import com.weareadaptive.auctionhouse.IntegrationTest;
import com.weareadaptive.auctionhouse.auction.CreateAuctionDTO;
import com.weareadaptive.auctionhouse.user.ReactorUserServiceGrpc.ReactorUserServiceStub;
import com.weareadaptive.auctionhouse.user.gRPCUserService.CreateUserRequest;
import com.weareadaptive.auctionhouse.user.gRPCUserService.GetUserRequest;
import com.weareadaptive.auctionhouse.user.gRPCUserService.UpdateUserAccessRequest;
import com.weareadaptive.auctionhouse.user.gRPCUserService.UpdateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static com.weareadaptive.auctionhouse.TestData.ADMIN_ID;
import static com.weareadaptive.auctionhouse.TestData.ORG_1;
import static com.weareadaptive.auctionhouse.TestData.UID_2;
import static com.weareadaptive.auctionhouse.TestData.UID_404;

class UserGRPCControllerTest extends IntegrationTest {
    private ReactorUserServiceStub stub;

    @BeforeEach
    void setUp() {
        stub = ReactorUserServiceGrpc.newReactorStub(channel);
    }

    private static final String USERNAME = "test";
    private static final String PASSWORD = "testPassword";

    private static CreateUserRequest createRequest(final String username, final String firstName, final String lastName,
                                                   final String password, final String organisationName,
                                                   final int userRole) {

        return CreateUserRequest.newBuilder()
                .setUsername(username).setFirstName(firstName).setLastName(lastName)
                .setOrganisationName(organisationName).setPassword(password).setUserRoleValue(userRole).build();
    }

    private static Stream<CreateUserRequest> postUserArgs() {
        return Stream.of(
                createRequest("", USERNAME, USERNAME, PASSWORD, ORG_1, 0),
                createRequest(USERNAME, "", USERNAME, PASSWORD, ORG_1, 0),
                createRequest(USERNAME, USERNAME, "", PASSWORD, ORG_1, 0),
                createRequest(USERNAME, USERNAME, USERNAME, "", ORG_1, 0),
                createRequest(USERNAME, USERNAME, USERNAME, PASSWORD, "", 0),
                createRequest(USERNAME, USERNAME, USERNAME, PASSWORD, ORG_1, 1),
                createRequest(USERNAME, USERNAME, USERNAME, PASSWORD, "ADMIN", 0));
    }

    @Test
    @DisplayName("GetUser returns an existing user")
    public void getUser() {
        final var source =
                stub.getUser(Mono.just(GetUserRequest.newBuilder().setUserId(1).build()));

        StepVerifier
                .create(source)
                .expectNextMatches(
                        o -> o.getUserId() == 1 && o.getUsername().equals("ADMIN") && o.getUserRoleValue() == 1)
                .thenCancel()
                .verify();
    }

    @Test
    @DisplayName("GetUser returns an error when requested user doesn't not exist")
    public void getNonExistentUser() {
        final var source =
                stub.getUser(Mono.just(GetUserRequest.newBuilder().setUserId(-1).build()));

        StepVerifier
                .create(source)
                .expectErrorMessage("NOT_FOUND: User not found")
                .verify();
    }

    @Test
    @DisplayName("PostUser_CreateUserWithValidInputs_ReturnsMessageAnd201")
    public void postUser() {
        final var source =
                stub.createUser(Mono.just(CreateUserRequest.newBuilder()
                        .setUsername("USER01")
                        .setUserRoleValue(0)
                        .setPassword("p")
                        .setOrganisationName(ORG_1)
                        .setLastName("L")
                        .setFirstName("F")
                        .build()));

        StepVerifier
                .create(source)
                .expectNextMatches(
                        o -> o.getUsername().equals("USER01") && o.getUserRoleValue() == 0)
                .verifyComplete();
    }

    @Test
    @DisplayName("PostUser_CreateAdminWithValidInputs_ReturnsMessageAnd201")
    public void postAdmin() {
        final var source =
                stub.createUser(Mono.just(CreateUserRequest.newBuilder()
                        .setUsername("ADMIN02")
                        .setUserRoleValue(1)
                        .setPassword("p")
                        .setLastName("L")
                        .setFirstName("F")
                        .setOrganisationName("ADMIN")
                        .build()));

        StepVerifier
                .create(source)
                .expectNextMatches(
                        o -> o.getUsername().equals("ADMIN02") && o.getUserRoleValue() == 1)
                .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("postUserArgs")
    @DisplayName("PostUser_createUserWithInvalidInputs_ReturnsMessageAnd400")
    public void postUserHandlesInvalidInputs(final CreateUserRequest request) {
        final var source = stub.createUser(Mono.just(request));

        StepVerifier
                .create(source)
                .expectErrorMatches(e -> e.getMessage().contains("INVALID_ARGUMENT"))
                .verify();
    }

    @Test()
    @DisplayName("PutUser returns updated user")
    public void putUser() {
        final var source =
                stub.updateUser(Mono.just(UpdateUserRequest.newBuilder()
                        .setUserId(1)
                        .setPassword("p")
                        .setLastName("L")
                        .setFirstName("F")
                        .build()));

        StepVerifier
                .create(source)
                .expectNextMatches(
                        o -> o.getUser().getUserId() == 1 && o.getUser().getFirstName().equals("F")
                                && o.getUser().getLastName().equals("L") && o.getPasswordChanged())
                .verifyComplete();
    }

    @Test()
    @DisplayName("PutUser_AttemptUpdateNonExistentUser_ReturnsMessageAnd404")
    public void putNonExistentUser() {
        final var source =
                stub.updateUser(Mono.just(UpdateUserRequest.newBuilder()
                        .setUserId(UID_404)
                        .build()));

        StepVerifier
                .create(source)
                .expectErrorMessage("NOT_FOUND: user not found");
    }

    @Test()
    @DisplayName("PutUserStatus_UpdateUserWithValidAccessStatus_ReturnsMessageAnd200")
    public void putUserStatus() {
        final var source =
                stub.updateUserAccess(Mono.just(UpdateUserAccessRequest.newBuilder()
                        .setUserId(ADMIN_ID)
                        .setAccessStatusValue(1)
                        .build()));

        StepVerifier
                .create(source)
                .expectNextMatches(o -> o.getAccessStatusValue() == 1)
                .verifyComplete();
    }

    @Test()
    @DisplayName("PutUserStatus_UpdateUserWithValidAccessStatus_ReturnsMessageAnd400")
    public void putUserStatusWithInvalidAccess() {
        final var source =
                stub.updateUserAccess(Mono.just(UpdateUserAccessRequest.newBuilder()
                        .setUserId(UID_2)
                        .setAccessStatusValue(-1)
                        .build()));

        StepVerifier
                .create(source)
                .expectErrorMessage("INVALID_ARGUMENT: userAccess value is invalid");
    }

    @Test()
    @DisplayName("GetUserAuctions returns all users auction")
    public void getUserAuctions() {
        final var source =
                stub.getUserAuctions(Mono.just(Empty.newBuilder().build()));

        context.getAuctionService()
                .createAuction(new CreateAuctionDTO(ADMIN_ID, "TEST", 0.1, 10));

        StepVerifier
                .create(source)
                .expectNextMatches(o -> o.getAuctionsCount() == 1 && o.getAuctions(0).getProduct().equals("TEST"));
    }

    @Test
    @DisplayName("Subscribing to GetUsersService emits an initial set of data")
    void getAllUsers() {
        final var source = stub.subscribeToGetUsersService(Flux.just(Empty.newBuilder().build()));

        StepVerifier
                .create(source)
                .expectNextMatches(u -> u.getInitialData() && u.getUsersCount() == 1)
                .thenCancel()
                .verify();
    }

    @Test
    @DisplayName("Adding a new user causes the GetUserService to emit")
    void getUsersServiceEmits() {
        final var source = stub.subscribeToGetUsersService(Flux.just(Empty.newBuilder().build()));

        stub.createUser(gRPCUserService.CreateUserRequest.newBuilder()
                        .setUsername("Queen")
                        .setPassword("password")
                        .setFirstName("Freddie")
                        .setLastName("Mercury")
                        .setOrganisationName("Bicycle")
                        .setUserRoleValue(0)
                        .build())
                .block();

        StepVerifier
                .create(source)
                .expectNextMatches(u -> u.getInitialData() && u.getUsersCount() == 1)
                .expectNextMatches(u -> !u.getInitialData() && u.getUsersCount() == 1 &&
                        u.getUsers(0).getUsername().equals("Queen"))
                .thenCancel()
                .verify();
    }

}
