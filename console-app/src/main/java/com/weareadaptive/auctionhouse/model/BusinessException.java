package com.weareadaptive.auctionhouse.model;

public class BusinessException extends RuntimeException {
  public BusinessException(final String message) {
    super(message);
  }
}
