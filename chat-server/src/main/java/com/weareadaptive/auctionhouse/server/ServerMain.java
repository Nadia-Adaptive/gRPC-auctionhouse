package com.weareadaptive.auctionhouse.server;

import java.util.logging.Logger;

public class ServerMain {
    static Logger log = Logger.getLogger("com.weareadaptive.server.ReceiverMain");
    public static void main(final String[] args) throws InterruptedException {
        ApplicationServer main = new ApplicationServer();

        try {
            main.start();
            main.blockUntilShutdown();
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            main.stop();
        }
    }
}

