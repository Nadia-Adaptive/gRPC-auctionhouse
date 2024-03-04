package com.weareadaptive.auctionhouse.organisation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.weareadaptive.auctionhouse.TestData.ORGANISATION1;
import static com.weareadaptive.auctionhouse.TestData.ORGANISATION2;
import static com.weareadaptive.auctionhouse.TestData.ORG_1;
import static com.weareadaptive.auctionhouse.TestData.ORG_2;
import static com.weareadaptive.auctionhouse.TestData.ORG_404;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrganisationRepositoryTest {
    private OrganisationRepository repository;

    @BeforeEach
    public void initState() {
        repository = new OrganisationRepository();
        repository.save(new Organisation(1, ORG_1));
    }

    @Test
    @DisplayName("findByName returns organisation")
    public void findByName() {
        assertEquals(1, repository.findByName(ORG_1).getOrganisationId());
    }

    @Test
    @DisplayName("findByName returns null if organisation does not exist")
    public void findByNameDoesNotExist() {
        assertNull(repository.findByName(ORG_404));
    }

    @Test
    @DisplayName("existByName returns true if organisation exists")
    public void existsByName() {
        assertTrue(repository.existsByName(ORG_1));
    }

    @Test
    @DisplayName("existsByName returns false if organisation does not exist")
    public void existsByNameDoesNotExist() {
        assertFalse(repository.existsByName(ORG_404));
    }

    @Test
    @DisplayName("save should add an organisation to the store")
    public void save() {
        repository.save(ORGANISATION2);
        assertEquals(ORGANISATION2, repository.findByName(ORG_2));
    }

    @Test
    @DisplayName("save should not add an existing organisation")
    public void saveExistingOrganisation() {
        repository.save(ORGANISATION1);
        assertEquals(1, repository.findAll().size());
    }
}
