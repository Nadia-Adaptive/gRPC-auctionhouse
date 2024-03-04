package com.weareadaptive.auctionhouse.organisation;

import com.google.protobuf.Empty;
import com.weareadaptive.auctionhouse.exception.NotFoundException;
import com.weareadaptive.auctionhouse.organisation.ReactorOrganisationServiceGrpc.OrganisationServiceImplBase;
import com.weareadaptive.auctionhouse.organisation.gRPCOrganisationService.GetOrganisationRequest;
import com.weareadaptive.auctionhouse.organisation.gRPCOrganisationService.OrganisationResponse;
import com.weareadaptive.auctionhouse.organisation.gRPCOrganisationService.OrganisationsResponse;
import com.weareadaptive.auctionhouse.utils.DTOMappers;
import io.grpc.Status;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.weareadaptive.auctionhouse.configuration.ApplicationContext.getApplicationContext;
import static com.weareadaptive.auctionhouse.observability.ApplicationLogger.info;
import static com.weareadaptive.auctionhouse.organisation.OrganisationMapper.mapToOrganisationResponse;
import static com.weareadaptive.auctionhouse.organisation.OrganisationMapper.mapToOrganisationsResponse;
import static java.lang.System.out;

public class OrganisationGRPCController extends OrganisationServiceImplBase {
    private final OrganisationService service = getApplicationContext().getOrganisationService();

    public OrganisationGRPCController() {
    }

    @Override
    public Flux<OrganisationsResponse> subscribeToGetOrganisationService(final Flux<Empty> request) {
        info("All organisations requested.");
        service.subscriptionSink.tryEmitNext(mapToOrganisationsResponse(service.getAll(), true));

        return service.subscriptionSink.asFlux().map((a) -> {
            out.println(a.getOrganisations(0).getOrganisationName() + " IS HERE!");
            return a;
        });
    }

    @Override
    public Mono<OrganisationResponse> getOrganisation(final Mono<GetOrganisationRequest> request) {
        return request.handle((r, sink) -> {
            info("Organisation with id " + r.getOrganisationId() + " requested.");
            try {
                sink.next(mapToOrganisationResponse(service.getOrganisation(r.getOrganisationId())));
            } catch (final NotFoundException e) {
                sink.error(DTOMappers.mapToGRPCError(Status.NOT_FOUND, e));
            }
        });
    }
}
