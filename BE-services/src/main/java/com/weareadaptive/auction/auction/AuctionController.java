package com.weareadaptive.auction.auction;

import com.weareadaptive.auction.bid.BidRequest;
import com.weareadaptive.auction.exception.BusinessException;
import com.weareadaptive.auction.response.Response;
import com.weareadaptive.auction.response.ResponseBuilder;
import com.weareadaptive.auction.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
@RequestMapping("/api/v1/auctions/")
public class AuctionController {
    AuctionService auctionService;
    Logger log = LoggerFactory.getLogger(AuctionController.class);

    AuctionController(final AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping("/")
    ResponseEntity<Response> createAuction(@RequestBody final AuctionRequest body) {
        log.info("Request to create auction with parameters {%s,%f,%d}".formatted(body.product(), body.minPrice(),
                body.quantity()));
        final var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final var auction = auctionService.createAuction(user.getId(), body);
        log.info("Auction created.");
        return ResponseBuilder.created(auction.id(), auction);
    }

    @GetMapping("/")
    ResponseEntity<Response> getAllAuctions() {
        log.info("All auctions requested.");
        final var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseBuilder.ok(auctionService.getAllAuctions(1));
    }

    @GetMapping("/{id}")
    ResponseEntity<Response> getAuction(@PathVariable final int id) {
        log.info("Auction with id " + id + "requested.");

        return ResponseBuilder.ok(auctionService.getAuction(id));
    }

    @GetMapping("/available")
    ResponseEntity<Response> getAllAvailableAuctions() {
        log.info("All available auctions requested.");
        final var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseBuilder.ok(auctionService.getAvailableAuctions(user.getId()));
    }

    @PutMapping("/{id}/bids")
    ResponseEntity<Response> putAuctionsMakeABid(@PathVariable final int id, @RequestBody final BidRequest body) {
        log.info("Request to bid on auction with id " + id);

        final var bidder = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseBuilder.ok(auctionService.makeABid(id, bidder.getId(), body));
    }

    @PutMapping("/{id}/close")
    ResponseEntity<Response> putCloseAuction(@PathVariable final int id) throws AccessDeniedException {
        log.info("Request to close auction with id " + id);

        final var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            final var auction = auctionService.closeAuction(id, user.getId());
            log.info("Auction closed.");
            return ResponseBuilder.ok(auction);
        } catch (final BusinessException e) {
            throw new AccessDeniedException("User doesn't own this resource");
        }
    }
}
