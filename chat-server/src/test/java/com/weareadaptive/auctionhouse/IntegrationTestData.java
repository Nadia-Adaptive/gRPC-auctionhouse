package com.weareadaptive.auctionhouse;

import com.github.javafaker.Faker;
import com.weareadaptive.auctionhouse.auction.AuctionService;
import com.weareadaptive.auctionhouse.auction.CreateAuctionDTO;
import com.weareadaptive.auctionhouse.authentication.AuthenticationService;
import com.weareadaptive.auctionhouse.organisation.OrganisationService;
import com.weareadaptive.auctionhouse.user.CreateUserDTO;
import com.weareadaptive.auctionhouse.user.User;
import com.weareadaptive.auctionhouse.user.UserRole;
import com.weareadaptive.auctionhouse.user.UserService;

import static com.weareadaptive.auctionhouse.configuration.ApplicationContext.getApplicationContext;

public class IntegrationTestData {
    public static String adminAuthToken;
    private final UserService userService;
    private final AuctionService auctionService;
    private final OrganisationService organisationService;
    private final AuthenticationService authenticationService;
    private final Faker faker;
    private User user1;

    public IntegrationTestData() {
        this.userService = getApplicationContext().getUserService();
        this.auctionService = getApplicationContext().getAuctionService();
        this.organisationService = getApplicationContext().getOrganisationService();
        this.authenticationService = getApplicationContext().getAuthenticationService();

        faker = new Faker();

        createInitData();
    }

    public void createInitData() {
        user1 = createRandomUser();

        organisationService.addOrganisation(TestData.ADMIN_ORG);
        organisationService.addOrganisation(TestData.ORG_1);

        final var admin =
                userService.createUser(
                        new CreateUserDTO("admin01", "admin", "admin", "admin", "ADMIN", UserRole.ADMIN));

        adminAuthToken = getToken(admin);
        auctionService.createAuction(new CreateAuctionDTO(user1.getUserId(), "TEST", 1.0, 10));
    }

    public String user1Token() {
        return getToken(user1);
    }

    public User createRandomUser() {
        var name = faker.name();
        return userService.createUser(new CreateUserDTO(
                "user01" + name.firstName(),
                TestData.PASSWORD,
                name.firstName(),
                name.lastName(),
                TestData.ORG_1,
                UserRole.USER)
        );
    }

    public String getToken(final User user) {
        return authenticationService.generateJWTToken(user.getUsername());
    }
}
