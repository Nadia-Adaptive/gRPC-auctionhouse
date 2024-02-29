package com.weareadaptive.auction.user;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

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


    @Test
    @DisplayName("findUserByUsername should return a userRole when passed valid credentials")
    public void shouldGetUserWhenPassedValidCredentials() {
        final var user = userRepository.findByUsername("ADMIN");
        assertEquals("ADMIN", user.getUsername());
        assertEquals(UserRole.ADMIN, user.getUserRole());
    }

    @Test
    @DisplayName("findUserByUsername should return null when passed invalid credentials")
    public void getUserByUsernamePassedInvalidUsername() {
        final var user = userRepository.findByUsername("notValid");
        assertEquals(null, user);
    }
}
