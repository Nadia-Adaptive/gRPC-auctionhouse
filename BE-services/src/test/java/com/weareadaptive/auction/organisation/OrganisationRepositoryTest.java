package com.weareadaptive.auction.organisation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static com.weareadaptive.auction.TestData.ADMIN_ORG;
import static com.weareadaptive.auction.TestData.ORG_1;
import static com.weareadaptive.auction.TestData.ORG_404;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class OrganisationRepositoryTest {
    @Autowired
    private OrganisationRepository repository;
    private Organisation organisation;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void beforeAll(){
        postgres.start();
    }

    @AfterAll
    static void afterAll(){
        postgres.stop();
    }

    @BeforeEach
    public void initState() {
        organisation = new Organisation(ORG_1);
    }
    @Test
    @DisplayName("findByName returns organisationName")
    public void findByName() {

        assertEquals(2, repository.findByName(ORG_1).get().getId());
    }

    @Test
    @DisplayName("findByName and organisationName does not exist returns empty optional")
    public void findByNameDoesNotExist() {
        assertTrue(repository.findByName(ORG_404).isEmpty());
    }

    @Test
    @DisplayName("existByName return true if organisationName exists")
    public void existsByName() {
        assertTrue(repository.existsByName(ADMIN_ORG));
    }

    @Test
    @DisplayName("GetOrganisationByName_OldOrganisationDoesNotExist_ReturnsNull")
    public void existsByNameDoesNotExist() {
        assertFalse(repository.existsByName(ORG_404));
    }
}
