package com.weareadaptive.auctionhouse.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.weareadaptive.auctionhouse.TestData.ADMIN;
import static com.weareadaptive.auctionhouse.TestData.ORG_1;
import static com.weareadaptive.auctionhouse.TestData.USER1;
import static com.weareadaptive.auctionhouse.TestData.USER2;
import static com.weareadaptive.auctionhouse.TestData.USER3;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrganisationStateTest {
    private OrganisationState state;

    @BeforeEach
    public void initState() {
        state = new OrganisationState();
        state.addUserToOrganisation(USER1);
    }

    @Test
    @DisplayName("addUserToOrg should create an organisation details object if it didn't already exist")
    public void shouldCreateNewOrganisationDetailIfItDoesntExist() {
        final var initialOrganisationCount = state.getAllOrganisations().count();
        state.addUserToOrganisation(USER3);
        assertNotEquals(2, initialOrganisationCount);
    }

    @Test
    @DisplayName("addUserToOrg should not create an organisation details object if it already exists")
    public void shouldNotCreateNewOrganisationDetailIfItExists() {
        final var initialOrganisationCount = state.getAllOrganisations().count();
        state.addUserToOrganisation(USER2);
        assertEquals(1, initialOrganisationCount);
    }

    @Test
    @DisplayName("addUserToOrg should not create an organisation for admins")
    public void shouldNotCreateAdminOrganisation() {
        final var initialOrganisationCount = state.getAllOrganisations().count();
        state.addUserToOrganisation(ADMIN);
        assertEquals(1, initialOrganisationCount);
    }

    @Test
    @DisplayName("removeUserFromOrganisation should remove existing user from specified organisation")
    public void removeExistingUserFromOrganisation() {
        assertTrue(state.removeUserFromOrganisation(USER1, ORG_1));
    }

    @Test
    @DisplayName("removeUserFromOrganisation should remove existing user from specified organisation")
    public void doNothingIfUserDoesntExist() {
        assertFalse(state.removeUserFromOrganisation(new User(15, "test", "password", "t", "t", "t"), ORG_1));
    }
}
