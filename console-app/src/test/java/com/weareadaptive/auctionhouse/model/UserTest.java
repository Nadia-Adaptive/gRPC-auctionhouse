package com.weareadaptive.auctionhouse.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {
    private static Stream<Arguments> createUserArguments() {
        return Stream.of(
                Arguments.of("username",
                        (Executable) () -> new User(1, null, "pp", "first", "last", "Org")),
                Arguments.of("firstName",
                        (Executable) () -> new User(1, "username", "pp", null, "last", "Org")),
                Arguments.of("lastName",
                        (Executable) () -> new User(1, "username", "pp", "first", null, "Org")),
                Arguments.of("organisation",
                        (Executable) () -> new User(1, "username", "pp", "first", "last", null)),
                Arguments.of("password",
                        (Executable) () -> new User(1, "username", null, "first", "last", "Org"))
        );
    }

    @ParameterizedTest(name = "Create user should throw when {0} is null")
    @MethodSource("createUserArguments")
    public void createUserShouldThrowWhenNullProperty(final String propertyName,
                                                      final Executable userExecutable) {
        //Act
        var exception = assertThrows(BusinessException.class, userExecutable);

        //Assert
        assertTrue(exception.getMessage().contains(propertyName));
    }

    @Test
    @DisplayName("ValidatePassword should return false when the password is not valid")
    public void shouldReturnFalseWhenThePasswordIsNotValid() {
        final var user = new User(1, "test", "thepassword", "Jonh", "Doe", "Adaptive");

        final var result = user.validatePassword("bad");

        assertFalse(result);
    }

    @Test
    @DisplayName("ValidatePassword should return true when the password is valid")
    public void shouldReturnTrueWhenThePasswordIsValid() {
        final var user = new User(1, "test", "thepassword", "Jonh", "Doe", "Adaptive");

        final var result = user.validatePassword("thepassword");

        assertTrue(result);
    }

    @Test
    @DisplayName("update should modify the user's details with the new values")
    public void shouldModifyUserWithNewFields() {
        final var user = new User(1, "test", "thepassword", "Jonh", "Doe", "Adaptive");
        final var testName = "Test user01";

        user.update(testName, "", "", "", "");

        assertTrue(user.getUsername().equals(testName));
    }

    @Test
    @DisplayName("update should not modify the user's details with the new values if values are empty")
    public void shouldNotModifyUserWithNewFieldsIfTheFieldsAreEmpty() {
        final var user = new User(1, "test", "thepassword", "Jonh", "Doe", "Adaptive");

        user.update("", "", "", "", "");

        assertTrue(user.getUsername().equals("test"));
    }

    @Test
    @DisplayName("a new user's default access status should be allowed")
    public void userDefaultStatusIsAllowed() {
        final var user = new User(1, "test", "thepassword", "Jonh", "Doe", "Adaptive");

        assertEquals(AccessStatus.ALLOWED, user.getAccessStatus());
    }
}
