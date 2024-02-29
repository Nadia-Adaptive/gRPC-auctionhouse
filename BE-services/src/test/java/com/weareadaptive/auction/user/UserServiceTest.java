package com.weareadaptive.auction.user;

import com.weareadaptive.auction.auction.AuctionRepository;
import com.weareadaptive.auction.exception.BusinessException;
import com.weareadaptive.auction.exception.NotFoundException;
import com.weareadaptive.auction.organisation.OrganisationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.weareadaptive.auction.TestData.AUCTION1;
import static com.weareadaptive.auction.TestData.AUCTION2;
import static com.weareadaptive.auction.TestData.ORGANISATION1;
import static com.weareadaptive.auction.TestData.ORGANISATION2;
import static com.weareadaptive.auction.TestData.ORG_1;
import static com.weareadaptive.auction.TestData.ORG_2;
import static com.weareadaptive.auction.TestData.UID_1;
import static com.weareadaptive.auction.TestData.UID_2;
import static com.weareadaptive.auction.TestData.UID_404;
import static com.weareadaptive.auction.TestData.USER1;
import static com.weareadaptive.auction.TestData.USER2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    private UserService userService;
    private OrganisationRepository organisationRepo;
    private UserAccessRequest accessRequest = new UserAccessRequest(AccessStatus.BLOCKED);
    private UserCreateRequest createRequest = new UserCreateRequest("password", "password",
            "firstName", "lastName", ORG_1, UserRole.USER);

    private UserUpdateRequest updateRequest = new UserUpdateRequest("", "test", "", ORG_2, UserRole.USER);

    @BeforeEach
    public void initState() {
        final var userRepo = mock(UserRepository.class);
        final var auctionRepo = mock(AuctionRepository.class);
        organisationRepo = mock(OrganisationRepository.class);

        when(userRepo.findByUsername(USER1.getUsername())).thenReturn(USER1);
        when(userRepo.findById(UID_1)).thenReturn(Optional.of(USER1));
        when(userRepo.findById(UID_2)).thenReturn(Optional.of(USER2));

        when(userRepo.existsById(UID_1)).thenReturn(true);
        when(userRepo.existsById(UID_2)).thenReturn(true);

        when(userRepo.save(any(User.class))).thenReturn(USER1);

        when(organisationRepo.findByName(ORG_1)).thenReturn(Optional.of(ORGANISATION1));
        when(organisationRepo.existsByName(ORG_1)).thenReturn(true);

        when(auctionRepo.findUserAuctions(UID_1)).thenReturn((List.of(AUCTION1, AUCTION2)));
        when(auctionRepo.findUserAuctions(UID_2)).thenReturn(List.of());

        userService = new UserService(userRepo, organisationRepo, auctionRepo);
    }

    @Test
    @DisplayName("getUser should return a userRole when passed a valid id")
    public void getUser() {
        final var user = userService.getUser(UID_1);
        assertEquals(USER1, user);
    }

    @Test
    @DisplayName("getUser should throw a BusinessException when passed an invalid id")
    public void getUserPassedInvalidId() {
        assertThrows(NotFoundException.class, () -> userService.getUser(-1));
    }

    @Test()
    @DisplayName("createUser should create a userRole when passed valid parameters")
    public void createUser() {
        final var user = userService.createUser(createRequest);

        final var organisation = organisationRepo.findByName(user.getOrganisationName()).orElse(null);

        assertEquals(ORGANISATION1, organisation);
    }

    @Test()
    @DisplayName("createUser should throws a business exception when passed invalid password")
    public void createUserPassedInvalidUsername() {
        assertThrows(BusinessException.class,
                () -> userService.createUser(new UserCreateRequest("user_name", "p", "f", "l", "o", UserRole.USER)));
    }

    @Test()
    @DisplayName("update should return a userRole when passed valid input")
    public void updateUser() {
        when(organisationRepo.findByName(ORG_2)).thenReturn(Optional.of(ORGANISATION2));
        when(organisationRepo.existsByName(ORG_2)).thenReturn(true);

        final var user = userService.updateUser(UID_1, updateRequest);
        assertEquals(ORGANISATION2, organisationRepo.findByName(user.getOrganisationName()).orElse(null));

        user.setOrganisationName(ORG_1);
    }

    @Test()
    @DisplayName("update should throws a business exception when passed invalid password")
    public void updateUserStatus() {
        final var user = userService.updateUserStatus(UID_1, accessRequest);
        assertEquals(AccessStatus.BLOCKED, user.getAccessStatus());
    }

    @Test()
    @DisplayName("update should throws a business exception when passed invalid password")
    public void updateUserStatusPassedInvalidId() {
        assertThrows(NotFoundException.class, () -> userService.updateUserStatus(UID_404, accessRequest));
    }

    @Test()
    @DisplayName("getUserAuctions should return all the userRole's auctions")
    public void getUserAuction() {
        assertEquals(2, userService.getUserAuctions(UID_1).size());
    }

    @Test()
    @DisplayName("getUserAuctions should return an empty list if  the userRole has no auctions")
    public void getUserAuctionNoAuctions() {
        assertTrue(userService.getUserAuctions(UID_2).isEmpty());
    }

    @Test()
    @DisplayName("getUserAuctions should throw if the userRole doesn't exist")
    public void getUserAuctionUserDoesntExist() {
        assertThrows(NotFoundException.class, () -> userService.getUserAuctions(-1));
    }
}
