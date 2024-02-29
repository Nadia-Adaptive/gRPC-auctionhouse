package com.weareadaptive.auctionhouse;

import com.weareadaptive.auctionhouse.utils.StringUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.weareadaptive.auctionhouse.TestData.ORGANISATION1;
import static com.weareadaptive.auctionhouse.TestData.USER1;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilTest {

    private static Stream<Arguments> testArguments() {
        return Stream.of(
                Arguments.of(null, true),
                Arguments.of("", true),
                Arguments.of("  ", true),
                Arguments.of("test", false)
        );
    }

    @ParameterizedTest(name = "{0} should return {1}")
    @MethodSource("testArguments")
    public void shouldTestStringIsNotNullOrBlank(final String input, final boolean expectedResult) {
        assertEquals(expectedResult, StringUtil.isNullOrEmpty(input));
    }

    @Test
    @DisplayName("should display user info in a particular format")
    public void shouldDisplayUserInfoInAParticularFormat() {
        assertEquals("Username: testuser1, First name: john, Last name: doe, Organisation: Org 1, Has access: Yes\n",
                StringUtil.userToString(USER1));
    }

    @Test
    @DisplayName("should display organisation info in a particular format")
    public void shouldDisplayOrganisationInfoInAParticularFormat() {
        assertEquals("Organisation: Org 1\n" + "Username: testuser1".indent(2),
                StringUtil.organisationToString(ORGANISATION1));
    }
}
