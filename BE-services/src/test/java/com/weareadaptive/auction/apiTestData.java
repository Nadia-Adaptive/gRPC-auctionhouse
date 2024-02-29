package com.weareadaptive.auction;

import com.github.javafaker.Faker;
import com.weareadaptive.auction.auction.AuctionRequest;
import com.weareadaptive.auction.auction.AuctionService;
import com.weareadaptive.auction.authentication.AuthenticationService;
import com.weareadaptive.auction.organisation.OrganisationService;
import com.weareadaptive.auction.user.User;
import com.weareadaptive.auction.user.UserCreateRequest;
import com.weareadaptive.auction.user.UserRole;
import com.weareadaptive.auction.user.UserService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import static com.weareadaptive.auction.TestData.ADMIN_ORG;
import static com.weareadaptive.auction.TestData.ORG_1;
import static com.weareadaptive.auction.TestData.PASSWORD;

@Component
public class apiTestData {
    public static String adminAuthToken;
    private final UserService userService;
    private final AuctionService auctionService;
    private final OrganisationService organisationService;
    private final AuthenticationService authenticationService;
    private final Faker faker;
    private User user1;

    public apiTestData(final UserService userService, final AuctionService auctionService,
                       final OrganisationService organisationService,
                       final AuthenticationService authenticationService) {
        this.userService = userService;
        this.auctionService = auctionService;
        this.organisationService = organisationService;
        this.authenticationService = authenticationService;

        faker = new Faker();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createInitData() {
        user1 = createRandomUser();

        organisationService.addOrganisation(ADMIN_ORG);
        organisationService.addOrganisation(ORG_1);

        final var admin =
                userService.createUser(
                        new UserCreateRequest("admin", "admin", "admin", "admin", "ADMIN", UserRole.ADMIN));

        adminAuthToken = getToken(admin);
        auctionService.createAuction(user1.getId(), new AuctionRequest("TEST", 1.0, 10));
    }

    public String user1Token() {
        return getToken(user1);
    }

    public User createRandomUser() {
        var name = faker.name();
        var user = userService.createUser(new UserCreateRequest(
                "user01" + name.firstName(),
                PASSWORD,
                name.firstName(),
                name.lastName(),
                ORG_1,
                UserRole.USER)
        );
        return user;
    }

    public String getToken(final User user) {
        return ResponseCookie.from("token", authenticationService.generateJWTToken(user.getUsername())).build()
                .toString();
    }
}
