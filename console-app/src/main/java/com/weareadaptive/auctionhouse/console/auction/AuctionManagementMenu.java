package com.weareadaptive.auctionhouse.console.auction;

import com.weareadaptive.auctionhouse.console.ConsoleMenu;
import com.weareadaptive.auctionhouse.console.MenuContext;
import com.weareadaptive.auctionhouse.model.Auction;
import com.weareadaptive.auctionhouse.model.AuctionStatus;
import com.weareadaptive.auctionhouse.model.Bid;

import java.util.Optional;

import static com.weareadaptive.auctionhouse.utils.AuctionManagementMenuFormatters.formatAuctionEntry;
import static com.weareadaptive.auctionhouse.utils.AuctionManagementMenuFormatters.formatBuyerBidEntry;
import static com.weareadaptive.auctionhouse.utils.AuctionManagementMenuFormatters.formatClosedAuctionSummary;
import static com.weareadaptive.auctionhouse.utils.AuctionManagementMenuFormatters.formatOpenAuctionSummary;
import static com.weareadaptive.auctionhouse.utils.AuctionManagementMenuFormatters.formatSellerBidEntry;
import static com.weareadaptive.auctionhouse.utils.PromptUtil.CANCEL_OPERATION_TEXT;
import static com.weareadaptive.auctionhouse.utils.PromptUtil.getDoubleInput;
import static com.weareadaptive.auctionhouse.utils.PromptUtil.getIntegerInput;
import static com.weareadaptive.auctionhouse.utils.PromptUtil.getStringInput;
import static com.weareadaptive.auctionhouse.utils.PromptUtil.hasUserTerminatedOperation;
import static com.weareadaptive.auctionhouse.utils.PromptUtil.INVALID_INPUT_MESSAGE;
import static com.weareadaptive.auctionhouse.utils.PromptUtil.TERMINATED_OPERATION_TEXT;


public class AuctionManagementMenu extends ConsoleMenu {
    @Override
    public void display(final MenuContext context) {
        createMenu(context,
                option("Create an auction", this::createAuction),
                option("See your auctions", this::listUserAuctions),
                option("Close an auction", this::closeAuction),
                option("Make a bid", this::placeABid),
                option("See won bids", this::listWonBids),
                option("See lost bids", this::listLostBids),
                leave("Go back"));
    }

    private void createAuction(final MenuContext context) {
        final var user = context.getCurrentUser();
        final var auctionState = context.getState().auctionState();
        final var out = context.getOut();

        out.println(CANCEL_OPERATION_TEXT);

        final String symbol = getStringInput(context, "What is the product's symbol?");
        if (hasUserTerminatedOperation(symbol)) {
            out.println(TERMINATED_OPERATION_TEXT);
            return;
        }

        final double price = getDoubleInput(context, "Enter the minimum asking price.");
        if (hasUserTerminatedOperation(price)) {
            out.println(TERMINATED_OPERATION_TEXT);
            return;
        }

        final int quantity = getIntegerInput(context, "How much %s are you selling?".formatted(symbol));
        if (hasUserTerminatedOperation(quantity)) {
            out.println(TERMINATED_OPERATION_TEXT);
            return;
        }

        final var newAuction = new Auction(auctionState.nextId(), user, symbol, price, quantity);

        auctionState.add(newAuction);
        out.println("Created new auction.");

        pressEnter(context);
    }

    private void listUserAuctions(final MenuContext context) {
        final var out = context.getOut();
        final var allAuctions =
                context.getState().auctionState().getUserAuctions(context.getCurrentUser().getUsername());

        out.println("=====> Your Auctions");
        out.println("===================================");
        allAuctions.forEach(a -> {
            if (a.getStatus() == AuctionStatus.OPEN) {
                out.printf(formatOpenAuctionSummary(a));
            } else {
                out.printf(formatClosedAuctionSummary(a));
            }
        });
        out.println("\n===================================");
        pressEnter(context);
    }

    private void closeAuction(final MenuContext context) {
        final var out = context.getOut();

        out.println("\n=====> Close an Auction");
        out.println(CANCEL_OPERATION_TEXT);
        out.println("Here are all your open auctions");

        out.println("==============================================");
        context.getState().auctionState().getUserAuctions(context.getCurrentUser().getUsername())
                .forEach(a -> out.println(formatAuctionEntry(a)));

        final var auction = getOwnAuction(context);
        if (auction.isEmpty()) {
            return;
        }

        out.println("Here is a summary of the auction.\n========");

        out.println(formatAuctionEntry(auction.get()));
        auction.get().getBids().forEach(b -> out.println(formatSellerBidEntry(b)));

        out.println("Do you wish to close it?");

        do {
            final var isClosed =
                    getStringInput(context, "Enter yes to close the auction or no to cancel the operation.");

            switch (isClosed.toLowerCase()) {
                case "yes" -> {
                    auction.get().close();
                    out.println("Closed the auction.");
                    return;
                }
                case "no" -> {
                    out.println(CANCEL_OPERATION_TEXT);
                    return;
                }
                default -> out.println(INVALID_INPUT_MESSAGE);
            }
        } while (true);
    }

    private void placeABid(final MenuContext context) {
        final var out = context.getOut();
        final var auctionState = context.getState().auctionState();
        final var user = context.getCurrentUser();
        final var timeContext = context.getTimeContext();

        if (auctionState.stream().findAny().isEmpty()) {
            out.println("There are no open auctions right now. Please try again later.");
            return;
        }

        out.println("Here's the list of available auctions.");

        out.println("================================");
        auctionState.getAvailableAuctions(user.getUsername())
                .forEach(a -> out.println(formatAuctionEntry(a)));

        out.println(CANCEL_OPERATION_TEXT);

        final var auction = getBiddableAuction(context);

        if (auction.isEmpty()) {
            return;
        }

        final var quantity = getIntegerInput(context, "Enter the quantity you're bidding for:");
        if (hasUserTerminatedOperation(quantity)) {
            return;
        }

        final var price = getOfferPrice(context, auction.get());

        if (hasUserTerminatedOperation(price)) {
            return;
        }

        final var bid = new Bid(user, price, quantity, timeContext.getNow());
        auction.get().placeABid(bid);
        out.println("Bid created");
        pressEnter(context);
    }

    private void listWonBids(final MenuContext context) {
        final var out = context.getOut();
        final var username = context.getCurrentUser().getUsername();
        final var auctions = context
                .getState().auctionState()
                .getAuctionsUserBidOn(username);

        out.println("Here are your winning bids.\n=======");

        auctions.forEach(a ->
                a.getWinningBids().forEach(b -> out.println(formatBuyerBidEntry(a, b))
                ));
    }

    private void listLostBids(final MenuContext context) {
        final var out = context.getOut();
        final var username = context.getCurrentUser().getUsername();
        final var auctions = context
                .getState().auctionState()
                .getAuctionsUserBidOn(username);

        out.println("Here are your losing bids.\n=======");

        auctions.forEach(a ->
                a.getLosingBids().forEach(b -> out.println((formatBuyerBidEntry(a, b)))
                ));
    }

    private double getOfferPrice(final MenuContext context, final Auction auction) {
        do {
            double offer =
                    getDoubleInput(context, "Enter your offer (min amount is %.2f):".formatted(auction.getMinPrice()));

            if (offer > auction.getMinPrice()) {
                return offer;
            }
        } while (true);
    }

    private Optional<Auction> getAuction(final MenuContext context) {
        final var state = context.getState().auctionState();
        final var out = context.getOut();
        do {
            final var auctionId = getIntegerInput(context, "Enter the auction id:", true);
            if (hasUserTerminatedOperation(auctionId)) {
                out.println(TERMINATED_OPERATION_TEXT);
                return Optional.empty();
            }

            if (state.hasAuction(auctionId)) {
                return Optional.of(state.get(auctionId));
            }
            out.println("Could not find auction. Please try again.");
        } while (true);
    }

    private Optional<Auction> getOwnAuction(final MenuContext context) {
        final var auction = getAuction(context);

        if (auction.isEmpty() || auction.get().getSeller().equals(context.getCurrentUser())) {
            return auction;
        } else {
            context.getOut().println("Could not find auction. Please try again.");
            return getOwnAuction(context);
        }
    }

    private Optional<Auction> getBiddableAuction(final MenuContext context) {
        final var auction = getAuction(context);

        if (auction.isEmpty() || !auction.get().getSeller().equals(context.getCurrentUser())) {
            return auction;
        } else {
            context.getOut().println("Could not find auction. Please try again.");
            return getBiddableAuction(context);
        }
    }
}
