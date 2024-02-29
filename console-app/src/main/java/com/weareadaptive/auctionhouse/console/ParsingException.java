package com.weareadaptive.auctionhouse.console;

public class ParsingException extends Exception {
  public ParsingException(final String property, final String type) {
    super(String.format("%s is not a valid %s", property, type));
  }
}
