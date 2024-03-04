package com.weareadaptive.auctionhouse.businesslogic;

import com.weareadaptive.auctionhouse.businesslogic.console.ConsoleAuction;

public class AuctionhouseConsoleApplication {
  public static void main(final String[] args) {
    ConsoleAuction consoleAuction = new ConsoleAuction();

    consoleAuction.start();
  }

}

