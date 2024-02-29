package com.weareadaptive.auctionhouse.console;

public final class Parser {

  private Parser() {
  }

  public static int parseInt(final String value, final String property) throws ParsingException {
    try {
      return Integer.parseInt(value);
    } catch (final NumberFormatException exception) {
      throw new ParsingException(property, "Integer");
    }
  }

  public static double parseDouble(final String value, final String property) throws ParsingException {
    try {
      return Double.parseDouble(value);
    } catch (final NumberFormatException exception) {
      throw new ParsingException(property, "Double");
    }
  }
}
