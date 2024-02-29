package com.weareadaptive.auction.organisation;

import com.weareadaptive.auction.exception.NotFoundException;
import com.weareadaptive.auction.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.weareadaptive.auction.TestData.ADMIN;
import static com.weareadaptive.auction.TestData.ORGANISATION1;
import static com.weareadaptive.auction.TestData.ORG_1;
import static com.weareadaptive.auction.TestData.USER1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrganisationServiceTest {
    OrganisationService service;

    Organisation newOrganisation = new Organisation("organisationName 48");


    @BeforeEach
    public void initState() {
        final var repo = mock(OrganisationRepository.class);
        final var userRepository = mock(UserRepository.class);

        when(repo.findByName(ORG_1)).thenReturn(Optional.of(ORGANISATION1));
        when(repo.findById(1)).thenReturn(Optional.of(ORGANISATION1));
        when(repo.save(any(Organisation.class))).thenReturn(newOrganisation);
        when(repo.existsByName(ORG_1)).thenReturn(true);
        when(repo.findAll()).thenReturn(List.of(ORGANISATION1, newOrganisation));

        when(userRepository.findAll()).thenReturn(List.of(USER1, ADMIN));


        service = new OrganisationService(repo, userRepository);
    }

    @Test
    @DisplayName("AddOrganisation_OrganisationDoesNotExist_ReturnsOrganisation")
    void addOrganisation() {
        final var organisation = service.addOrganisation(newOrganisation.getName());
        assertEquals(newOrganisation, organisation);
    }

    @Test
    @DisplayName("AddOrganisation_OrganisationDoesExist_ReturnsExistingOrganisation")
    void addOrganisationThatExists() {
        assertEquals(ORGANISATION1, service.addOrganisation(ORG_1));
    }

    @Test
    @DisplayName("getOrganisation returns organisationName")
    void getOrganisation() {
        final var organisation = service.getOrganisation(1);
        assertEquals(ORGANISATION1.getName(), organisation.name());
        assertEquals(1, organisation.users().size());
    }

    @Test
    @DisplayName("getOrganisation and organisationName doesn't exist returns organisationName")
    void getOrganisationThatDoesNotExist() {
        assertThrows(NotFoundException.class, () -> service.getOrganisation(-13));
    }

    @Test
    @DisplayName("getAllOrganisations returns organisations")
    void getAllOrganisations() {
        assertEquals(2, service.getAll().size());
    }

    @Test
    @DisplayName("getOrganisationByName returns organisationName")
    void getOrganisationByName() {
        final var organisation = service.getOrganisation(ORG_1);
        assertEquals(ORGANISATION1.getId(), organisation.id());
        assertEquals(ORGANISATION1.getName(), organisation.name());
        assertEquals(1, organisation.users().size());
    }

    @Test
    @DisplayName("GetOrganisationByName and organisationName doesn't exist throws")
    void getOrganisationByNameThatDoesNotExist() {
        assertThrows(NotFoundException.class, () -> service.getOrganisation("fail"));
    }
}
