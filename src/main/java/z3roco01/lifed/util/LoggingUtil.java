package z3roco01.lifed.util;

import z3roco01.lifed.Lifed;

/**
 * Handles logging, to the console or admins, or both
 */
public class LoggingUtil {
    /**
     * Logs a message to where ever it should go
     * @param msg the message string
     */
    public static void log(String msg) {
        // for now only log it
        Lifed.LOGGER.info(msg);
    }
}
