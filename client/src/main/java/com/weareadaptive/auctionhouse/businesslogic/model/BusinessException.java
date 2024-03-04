package com.weareadaptive.auctionhouse.businesslogic.model;

public class BusinessException extends RuntimeException {
  public BusinessException(final String message) {
    super(message);
  }
}
