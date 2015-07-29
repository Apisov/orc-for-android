package com.microsoft.services.orc.log;

/**
 * The interface Logger.
 */
public interface Logger {
    /**
     * Log void.
     *
     * @param content the content
     * @param logLevel the log level
     */
    void log(String content, LogLevel logLevel);
}
