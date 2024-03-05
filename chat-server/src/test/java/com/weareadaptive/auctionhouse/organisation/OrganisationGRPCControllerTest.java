package com.weareadaptive.auctionhouse.organisation;

import com.google.protobuf.Empty;
import com.weareadaptive.auctionhouse.IntegrationTest;
import com.weareadaptive.auctionhouse.organisation.ReactorOrganisationServiceGrpc.ReactorOrganisationServiceStub;
import com.weareadaptive.auctionhouse.organisation.gRPCOrganisationService.GetOrganisationRequest;
import com.weareadaptive.auctionhouse.user.ReactorUserServiceGrpc;
import com.weareadaptive.auctionhouse.user.gRPCUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


class OrganisationGRPCControllerTest extends IntegrationTest {
    private ReactorOrganisationServiceStub stub;

    @BeforeEach
    void setUp() {
        stub = ReactorOrganisationServiceGrpc.newReactorStub(channel);
    }

    @Test
    @DisplayName("Subscribing to GetOrganisationService emits an initial set of data")
    void getAllOrganisations() {
        final var source = stub.subscribeToGetOrganisationService(Flux.just(Empty.newBuilder().build()));

        StepVerifier
                .create(source)
                .expectNextMatches(o -> o.getInitialData() && o.getOrganisationsList().size() == 1)
                .thenCancel()
                .verify();
    }

    @Test
    @DisplayName("GetOrganisation returns an organisation")
    void getOrganisation() {
        final var source =
                stub.getOrganisation(Mono.just(GetOrganisationRequest.newBuilder().setOrganisationId(1).build()));

        StepVerifier
                .create(source)
                .expectNextMatches(o -> o.getOrganisationName().equals("ADMIN"))
                .verifyComplete();
    }

    @Test
    @DisplayName("GetOrganisation returns an error message when organisation does not exist")
    void getOrganisationInvalidId() {
        final var source =
                stub.getOrganisation(Mono.just(GetOrganisationRequest.newBuilder().setOrganisationId(-1).build()));

        StepVerifier
                .create(source)
                .expectErrorMessage("NOT_FOUND: Organisation not found")
                .verify();
    }

    @Test
    @DisplayName("Adding a new organisation causes the GetOrganisationService to emit")
    void getOrganisationServiceEmit() {
        final var source =
                stub.subscribeToGetOrganisationService(Flux.just(Empty.newBuilder().build()));

        ReactorUserServiceGrpc.ReactorUserServiceStub stub = ReactorUserServiceGrpc.newReactorStub(channel);
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
                .expectNextMatches(o -> o.getInitialData() && o.getOrganisationsList().size() == 1)
                .expectNextMatches(o -> !o.getInitialData() && o.getOrganisationsList().size() == 1)
                .thenCancel()
                .verify();
    }
}
