package com.weareadaptive.auctionhouse.auction;

import com.google.protobuf.Empty;
import com.weareadaptive.auctionhouse.Auction.ReactorAuctionServiceGrpc.AuctionServiceImplBase;
import com.weareadaptive.auctionhouse.Auction.gRPCAuctionService.AuctionResponse;
import com.weareadaptive.auctionhouse.Auction.gRPCAuctionService.AuctionsResponse;
import com.weareadaptive.auctionhouse.Auction.gRPCAuctionService.CloseAuctionRequest;
import com.weareadaptive.auctionhouse.Auction.gRPCAuctionService.CreateAuctionRequest;
import com.weareadaptive.auctionhouse.Auction.gRPCAuctionService.DeleteAuctionRequest;
import com.weareadaptive.auctionhouse.Auction.gRPCAuctionService.GetAuctionRequest;
import com.weareadaptive.auctionhouse.Auction.gRPCBidService;
import com.weareadaptive.auctionhouse.configuration.ApplicationContext;
import com.weareadaptive.auctionhouse.exception.BusinessException;
import com.weareadaptive.auctionhouse.exception.InternalErrorException;
import com.weareadaptive.auctionhouse.exception.NotFoundException;
import com.weareadaptive.auctionhouse.observability.ApplicationLogger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import static com.weareadaptive.auctionhouse.auction.AuctionMapper.mapToAuctionResponse;
import static com.weareadaptive.auctionhouse.auction.AuctionMapper.mapToCreateAuctionDTO;
import static com.weareadaptive.auctionhouse.observability.ApplicationLogger.getLogger;
import static com.weareadaptive.auctionhouse.observability.ApplicationLogger.info;
import static com.weareadaptive.auctionhouse.server.RequestHeader.CTX_CLIENT_ID;
import static com.weareadaptive.auctionhouse.utils.DTOMappers.mapToGRPCError;
import static io.grpc.Status.INTERNAL;
import static io.grpc.Status.NOT_FOUND;
import static io.grpc.Status.PERMISSION_DENIED;
import static io.grpc.Status.UNAUTHENTICATED;


public class AuctionGRPCController extends AuctionServiceImplBase {
    private final AuctionService auctionService;

    private final Sinks.Many<AuctionsResponse> auctionSink = Sinks.many().multicast().onBackpressureBuffer();

    AuctionGRPCController() {
        auctionService = ApplicationContext.getApplicationContext().getAuctionService();
    }

    @Override
    public Mono<AuctionResponse> createAuction(final Mono<CreateAuctionRequest> request) {
        return request.log(ApplicationLogger.getLogger())
                .handle((r, sink) -> {
                    info("Request to create auction");
                    final var userId = CTX_CLIENT_ID.get();
                    try {
                        final var auctionDTO = mapToCreateAuctionDTO(userId, r);
                        auctionService.createAuction(auctionDTO);
                    } catch (final InternalErrorException e) {
                        info("Auction created.");
                        sink.error(mapToGRPCError(INTERNAL, e));
                    }
                });
    }

    @Override
    public Mono<AuctionResponse> deleteAuction(final Mono<DeleteAuctionRequest> request) {
        return request.handle((r, sink) -> {
            try {
                info("Attempting to delete auction with id " + r.getAuctionId());
                final var userId = CTX_CLIENT_ID.get();
                final var deleteReq = AuctionMapper.mapToDeleteAuctionDTO(userId, r);
                sink.next(mapToAuctionResponse(auctionService.deleteAuction(deleteReq)));
            } catch (final NotFoundException e) {

            }
        });
    }

    @Override
    public Flux<AuctionsResponse> subscribeToGetAuctionsService(final Flux<Empty> request) {
        info("All auctions requested.");

        return auctionSink.asFlux(); //.filter(a->a.)
    }

    @Override
    public Flux<gRPCBidService.BidsResponse> subscribeToAuctionUpdateService(final Flux<Empty> request) {
        return super.subscribeToAuctionUpdateService(request);
    }

    @Override
    public Mono<AuctionResponse> getAuction(final Mono<GetAuctionRequest> request) {
        return request.handle((r, sink) -> {
            info("Auction with id " + r.getAuctionId() + "requested.");
            try {
                sink.next(mapToAuctionResponse(auctionService.getAuction(r.getAuctionId())));
            } catch (final BusinessException e) {
                sink.error(e);
            }
        });
    }
    @Override
    public Mono<AuctionResponse> closeAuction(final Mono<CloseAuctionRequest> request) {
        return request.log(getLogger()).handle((r, sink) -> {
            try {
                final var userId = CTX_CLIENT_ID.get();
                info("Request to close auction with id " + r.getAuctionId());

                sink.next(mapToAuctionResponse(auctionService.closeAuction(r.getAuctionId(), userId)));
                info("Auction closed.");
            } catch (final BusinessException e) {
                sink.error(mapToGRPCError(PERMISSION_DENIED, e));
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
