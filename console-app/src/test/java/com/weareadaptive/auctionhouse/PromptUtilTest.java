package com.weareadaptive.auctionhouse;

import com.weareadaptive.auctionhouse.console.MenuContext;
import com.weareadaptive.auctionhouse.model.InstantTimeProvider;
import com.weareadaptive.auctionhouse.model.TimeContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Scanner;


import static com.weareadaptive.auctionhouse.utils.PromptUtil.getDoubleInput;
import static com.weareadaptive.auctionhouse.utils.PromptUtil.getIntegerInput;
import static com.weareadaptive.auctionhouse.utils.PromptUtil.getStringOrEmptyInput;
import static com.weareadaptive.auctionhouse.utils.PromptUtil.hasUserTerminatedOperation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PromptUtilTest {
    MenuContext createMenuContext(final String src) {
        return new MenuContext(null, new Scanner(src), System.out, new TimeContext(new InstantTimeProvider()));
    }

    @Test
    @DisplayName("getIntegerInput should parse user data into int")
    public void shouldParseUserDataIntoInt() {
        final var context = createMenuContext("1");
        ;
        var userInt = getIntegerInput(context, "Text");
        assertEquals(userInt, 1);
    }

    @Test
    @DisplayName("getIntegerInput should run recursively until valid input is given")
    public void shouldRunRecursivelyUntilValid() {
        final var context = createMenuContext("t\n e\n st\n 1");

        final var userInt = getIntegerInput(context, "Text");
        assertEquals(1, userInt);
    }

    @Test
    @DisplayName("getStringOrEmptyInput should return an empty optional when cancelled.")
    public void shouldReturnVoidWhenCancelled() {
        final var context = createMenuContext("q");

        var userInput = getStringOrEmptyInput(context, "Text");
        assertTrue(userInput.isEmpty());
    }

    @Test
    @DisplayName("getDoubleInput should parse user data into int")
    public void shouldParseUserInputIntoDouble() {
        final var context = createMenuContext("0.1");

        final var userDouble = getDoubleInput(context, "Text");
        assertEquals(0.1d, userDouble);
    }

    @Test
    @DisplayName("getDoubleInput should return false when user inputs q instead of a double")
    public void shouldReturnFalseWhenUserInputsQInsteadOfDouble() {
        final var context = createMenuContext("q");

        final var userDouble = getDoubleInput(context, "Text");
        assertTrue(hasUserTerminatedOperation(userDouble));
    }

    @Test
    @DisplayName("getIntegerInput should return false when user inputs q instead of an int")
    public void shouldReturnFalseWhenUserInputsQInsteadOfInt() {
        final var context = createMenuContext("q");

        final var userInt = getIntegerInput(context, "Text");
        assertTrue(hasUserTerminatedOperation(userInt));
    }

    @Test
    @DisplayName("getIntegerInput should loop if negative values are entered")
    public void shouldLoopIfNegativeIntValuesEntered() {
        final var context = createMenuContext("-10\n-1\n-9\n1");

        final var userInt = getIntegerInput(context, "Text");
        assertEquals(1, userInt);
    }

    @Test
    @DisplayName("getDoubleInput should loop if negative values are entered")
    public void shouldLoopIfNegativeDoubleValuesEntered() {
        final var context = createMenuContext("-10.8\n-0.1\n-8.9\n0.01");

        final var userInt = getDoubleInput(context, "Text");
        assertEquals(0.01, userInt);
    }

    @Test
    @DisplayName("getIntegerInput should return zero if allowZero is true")
    public void shouldReturnZeroIntegerIfAllowZeroIsSetToTrue() {
        final var context = createMenuContext("0");

        final var userInt = getIntegerInput(context, "Text", true);
        assertEquals(0, userInt);
    }

    @Test
    @DisplayName("getIntegerInput should loop if allowZero is false")
    public void shouldLoopIfAllowZeroIsSetToFalse() {
        final var context = createMenuContext("0\n1");

        final var userInt = getIntegerInput(context, "Text");
        assertEquals(1, userInt);
    }
}
