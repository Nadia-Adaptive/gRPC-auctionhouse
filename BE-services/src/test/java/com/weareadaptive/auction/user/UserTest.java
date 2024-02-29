package com.weareadaptive.auction.user;

import com.weareadaptive.auction.exception.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.stream.Stream;

import static com.weareadaptive.auction.TestData.ORG_1;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {
    private static Stream<Arguments> createUserArguments() {
        return Stream.of(
                Arguments.of("password",
                        (Executable) () -> new User(null, "pp", "first", "last", ORG_1, UserRole.USER, Instant.now())),
                Arguments.of("firstName",
                        (Executable) () -> new User("password", "pp", null, "last", ORG_1, UserRole.USER,
                                Instant.now())),
                Arguments.of("lastName",
                        (Executable) () -> new User("password", "pp", "first", null, ORG_1, UserRole.USER,
                                Instant.now())),
                Arguments.of("organisationName",
                        (Executable) () -> new User("password", "pp", "first", "last", null, UserRole.USER,
                                Instant.now())),
                Arguments.of("password",
                        (Executable) () -> new User("password", null, "first", "last", ORG_1, UserRole.USER,
                                Instant.now())),
                Arguments.of("password",
                        (Executable) () -> new User("password", null, "first", "last", ORG_1, UserRole.USER, null))
        );
    }

    @ParameterizedTest(name = "Create userRole should throw when {0} is null")
    @MethodSource("createUserArguments")
    public void createUserShouldThrowWhenNullProperty(final String propertyName, final Executable userExecutable) {
        var exception = assertThrows(BusinessException.class, userExecutable);

        assertTrue(exception.getMessage().contains(propertyName));
    }

    @Test
    @DisplayName("ValidatePassword should return false when the password is not valid")
    public void shouldReturnFalseWhenThePasswordIsNotValid() {
        final var user = new User("test", "password", "John", "Doe", ORG_1, UserRole.USER, Instant.now());
        final var result = user.validatePassword("bad");

        assertFalse(result);
    }

    @Test
    @DisplayName("ValidatePassword should return true when the password is valid")
    public void shouldReturnTrueWhenThePasswordIsValid() {
        final var user = new User("test", "password", "John", "Doe", ORG_1, UserRole.USER, Instant.now());

        final var result = user.validatePassword("password");

        assertTrue(result);
    }

    @Test
    @DisplayName("a new userRole's default access status should be allowed")
    public void userDefaultStatusIsAllowed() {
        final var user = new User("test", "password", "John", "Doe", ORG_1, UserRole.USER, Instant.now());
        Assertions.assertEquals(AccessStatus.ALLOWED, user.getAccessStatus());
    }
}
