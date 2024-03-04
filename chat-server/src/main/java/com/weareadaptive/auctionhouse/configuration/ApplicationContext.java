package com.weareadaptive.auctionhouse.configuration;

import com.weareadaptive.auctionhouse.auction.AuctionRepository;
import com.weareadaptive.auctionhouse.auction.AuctionService;
import com.weareadaptive.auctionhouse.authentication.AuthenticationService;
import com.weareadaptive.auctionhouse.bid.BidRepository;
import com.weareadaptive.auctionhouse.organisation.Organisation;
import com.weareadaptive.auctionhouse.organisation.OrganisationRepository;
import com.weareadaptive.auctionhouse.organisation.OrganisationService;

import com.weareadaptive.auctionhouse.user.User;
import com.weareadaptive.auctionhouse.user.UserRepository;
import com.weareadaptive.auctionhouse.user.UserRole;
import com.weareadaptive.auctionhouse.user.UserService;

public final class ApplicationContext {
    private final UserRepository userRepo;
    private final OrganisationRepository organisationRepo;
    private final AuthenticationService authenticationService;
    private static ApplicationContext context;
    private final UserService userService;
    private final AuctionRepository auctionRepo;
    private final OrganisationService organisationService;
    private final AuctionService auctionService;
    private final BidRepository bidRepo;

    private ApplicationContext() {
        // Initialise repositories
        userRepo = new UserRepository();
        organisationRepo = new OrganisationRepository();
        auctionRepo = new AuctionRepository();
        bidRepo = new BidRepository();

        // Initialise services
        authenticationService = new AuthenticationService(userRepo);
        userService = new UserService(userRepo, organisationRepo, auctionRepo);
        organisationService = new OrganisationService(organisationRepo, userRepo);
        auctionService = new AuctionService(auctionRepo, userRepo, bidRepo);

        // Setup data
        organisationRepo.save(new Organisation(organisationRepo.nextId(), "ADMIN"));
        userRepo.save(new User(userRepo.nextId(), "ADMIN", "password", "admin", "admin", "ADMIN", UserRole.ADMIN));
    }

    public static ApplicationContext getApplicationContext() {
        if (context == null) {
            context = new ApplicationContext();
        }
        return context;
    }

    public UserRepository getUserRepo() {
        return userRepo;
    }

    public OrganisationRepository getOrganisationRepo() {
        return organisationRepo;
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public OrganisationService getOrganisationService() {
        return organisationService;
    }

    public AuctionService getAuctionService() {
        return auctionService;
    }

    public UserService getUserService() {
        return userService;
    }
}
