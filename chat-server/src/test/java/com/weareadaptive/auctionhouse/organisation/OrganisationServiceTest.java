package com.weareadaptive.auctionhouse.organisation;

import com.weareadaptive.auctionhouse.TestData;
import com.weareadaptive.auctionhouse.user.UserRepository;
import com.weareadaptive.auctionhouse.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrganisationServiceTest {
    OrganisationService service;

    Organisation newOrganisation = new Organisation(1, "organisationName 48");


    @BeforeEach
    public void initState() {
        final var repo = mock(OrganisationRepository.class);
        final var userRepository = Mockito.mock(UserRepository.class);

        when(repo.findByName(TestData.ORG_1)).thenReturn(TestData.ORGANISATION1);
        when(repo.findById(1)).thenReturn(TestData.ORGANISATION1);
        when(repo.save(any(Organisation.class))).thenReturn(newOrganisation);
        when(repo.existsByName(TestData.ORG_1)).thenReturn(true);
        when(repo.findAll()).thenReturn(List.of(TestData.ORGANISATION1, newOrganisation));

        when(userRepository.findAll()).thenReturn(List.of(TestData.USER1, TestData.ADMIN));


        service = new OrganisationService(repo, userRepository);
    }

    @Test
    @DisplayName("AddOrganisation_OrganisationDoesNotExist_ReturnsOrganisation")
    void addOrganisation() {
        final var organisation = service.addOrganisation(newOrganisation.getOrganisationName());
        assertEquals(newOrganisation, organisation);
    }

    @Test
    @DisplayName("AddOrganisation_OrganisationDoesExist_ReturnsExistingOrganisation")
    void addOrganisationThatExists() {
        assertEquals(TestData.ORGANISATION1, service.addOrganisation(TestData.ORG_1));
    }

    @Test
    @DisplayName("getOrganisation returns organisationName")
    void getOrganisation() {
        final var dto = service.getOrganisation(TestData.ORG_1);
        final var organisation = dto.organisation();
        final var users = dto.users();
        assertEquals(TestData.ORGANISATION1.getOrganisationName(), organisation.getOrganisationName());
        assertEquals(1, users.size());
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
        final var dto = service.getOrganisation(TestData.ORG_1);
        final var organisation = dto.organisation();
        final var users = dto.users();
        assertEquals(TestData.ORGANISATION1.getOrganisationId(), organisation.getOrganisationId());
        assertEquals(TestData.ORGANISATION1.getOrganisationName(), organisation.getOrganisationName());
        assertEquals(1, users.size());
    }

    @Test
    @DisplayName("GetOrganisationByName and organisationName doesn't exist throws")
    void getOrganisationByNameThatDoesNotExist() {
        assertThrows(NotFoundException.class, () -> service.getOrganisation("fail"));
    }
}
