package com.weareadaptive.auctionhouse.bid;

import com.weareadaptive.auctionhouse.Auction.ReactorBidServiceGrpc.BidServiceImplBase;
import com.weareadaptive.auctionhouse.Auction.gRPCBidService.BidResponse;
import com.weareadaptive.auctionhouse.Auction.gRPCBidService.CreateBidRequest;
import com.weareadaptive.auctionhouse.auction.AuctionService;
import com.weareadaptive.auctionhouse.exception.BusinessException;
import reactor.core.publisher.Mono;

import static com.weareadaptive.auctionhouse.bid.BidMapper.mapToBidRequest;
import static com.weareadaptive.auctionhouse.bid.BidMapper.mapToBidResponse;
import static com.weareadaptive.auctionhouse.configuration.ApplicationContext.getApplicationContext;
import static com.weareadaptive.auctionhouse.observability.ApplicationLogger.info;
import static com.weareadaptive.auctionhouse.server.RequestHeader.CTX_CLIENT_ID;
import static com.weareadaptive.auctionhouse.utils.DTOMappers.mapToGRPCError;
import static io.grpc.Status.INTERNAL;
import static io.grpc.Status.PERMISSION_DENIED;

public class BidGRPCService extends BidServiceImplBase {
    AuctionService auctionService;

    public BidGRPCService() {
        auctionService = getApplicationContext().getAuctionService();
    }

    @Override
    public Mono<BidResponse> createBid(final Mono<CreateBidRequest> request) {
        return request.handle((r, sink) -> {
            try {
                final var userId = CTX_CLIENT_ID.get();
                final var bid = mapToBidRequest(userId, r);
                info("Request to bid on auction with id " + bid.auctionId());
                sink.next(mapToBidResponse(auctionService.makeABid((bid))));
            } catch (final BusinessException e) {
                sink.error(mapToGRPCError(PERMISSION_DENIED, e));
            } catch (final NullPointerException e) {
                sink.error(mapToGRPCError(INTERNAL, e));
            }
        });
    }
}
