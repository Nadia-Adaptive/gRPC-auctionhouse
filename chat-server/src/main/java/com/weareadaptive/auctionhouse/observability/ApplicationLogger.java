package com.weareadaptive.auctionhouse.observability;

import reactor.util.Logger;
import reactor.util.Loggers;

public final class ApplicationLogger {
    private static Logger logger = Loggers.getLogger("Application Logger");

    private ApplicationLogger() {
    }


    public static void info(final String log) {
        ApplicationLogger.logger.info(log);
    }

    public static void error(final String log) {
        ApplicationLogger.logger.error(log);
    }

    public static void warn(final String log) {
        ApplicationLogger.logger.warn(log);
    }

    public static Logger getLogger() {
        return logger;
    }
}
