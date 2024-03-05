package com.weareadaptive.auctionhouse.user;

import com.weareadaptive.auctionhouse.auction.AuctionRepository;
import com.weareadaptive.auctionhouse.exception.BusinessException;
import com.weareadaptive.auctionhouse.exception.NotFoundException;
import com.weareadaptive.auctionhouse.organisation.OrganisationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.weareadaptive.auctionhouse.TestData.AUCTION1;
import static com.weareadaptive.auctionhouse.TestData.AUCTION2;
import static com.weareadaptive.auctionhouse.TestData.ORGANISATION1;
import static com.weareadaptive.auctionhouse.TestData.ORGANISATION2;
import static com.weareadaptive.auctionhouse.TestData.ORG_1;
import static com.weareadaptive.auctionhouse.TestData.ORG_2;
import static com.weareadaptive.auctionhouse.TestData.UID_1;
import static com.weareadaptive.auctionhouse.TestData.UID_2;
import static com.weareadaptive.auctionhouse.TestData.UID_404;
import static com.weareadaptive.auctionhouse.TestData.USER1;
import static com.weareadaptive.auctionhouse.TestData.USER2;
import static com.weareadaptive.auctionhouse.user.AccessStatus.BLOCKED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    private UserService userService;
    private OrganisationRepository organisationRepo;
    private UserAccessDTO accessRequest = new UserAccessDTO(UID_1, BLOCKED);
    private CreateUserDTO createRequest = new CreateUserDTO("password", "password",
            "firstName", "lastName", ORG_1, UserRole.USER);

    private UserUpdateDTO updateRequest = new UserUpdateDTO(UID_1, "test", "", "", ORG_2);

    @BeforeEach
    public void initState() {
        final var userRepo = mock(UserRepository.class);
        final var auctionRepo = mock(AuctionRepository.class);
        organisationRepo = mock(OrganisationRepository.class);

        when(userRepo.findByUsername(USER1.getUsername())).thenReturn(USER1);
        when(userRepo.findById(UID_1)).thenReturn(USER1);
        when(userRepo.findById(UID_2)).thenReturn(USER2);

        when(userRepo.existsById(UID_1)).thenReturn(true);
        when(userRepo.existsById(UID_2)).thenReturn(true);

        when(userRepo.save(any(User.class))).thenReturn(USER1);

        when(organisationRepo.findByName(ORG_1)).thenReturn(ORGANISATION1);
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

        final var organisation = organisationRepo.findByName(user.getOrganisationName());

        assertEquals(ORGANISATION1, organisation);
    }

    @Test()
    @DisplayName("createUser should throws a business exception when passed invalid password")
    public void createUserPassedInvalidUsername() {
        assertThrows(BusinessException.class,
                () -> userService.createUser(new CreateUserDTO("user_name", "p", "f", "l", "o", UserRole.USER)));
    }

    @Test()
    @DisplayName("update should return a userRole when passed valid input")
    public void updateUser() {
        when(organisationRepo.findByName(ORG_2)).thenReturn(ORGANISATION2);
        when(organisationRepo.existsByName(ORG_2)).thenReturn(true);

        final var user = userService.updateUser(updateRequest);
        assertEquals(ORGANISATION2, organisationRepo.findByName(user.getOrganisationName()));

        user.setOrganisationName(ORG_1);
    }

    @Test()
    @DisplayName("updateUserStatus changes a user's access status")
    public void updateUserStatus() {
        final var user = userService.updateUserStatus(accessRequest);
        assertEquals(BLOCKED, user.getAccessStatus());
    }

    @Test()
    @DisplayName("update throws when passed invalid id")
    public void updateUserStatusPassedInvalidId() {
        assertThrows(NotFoundException.class, () -> userService.updateUserStatus(new UserAccessDTO(UID_404, BLOCKED)));
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
