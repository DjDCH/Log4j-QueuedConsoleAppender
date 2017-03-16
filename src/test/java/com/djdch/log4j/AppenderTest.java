package com.djdch.log4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import static org.junit.Assert.assertEquals;

public class AppenderTest {
    private static final Logger logger = LogManager.getLogger();

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Before
    public void setUpAppender() throws IOException {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        final LoggerConfig root = config.getRootLogger();

        PatternLayout layout = PatternLayout.newBuilder().withPattern("[%p] %m%n").withConfiguration(config).build();
        QueuedConsoleAppender appender = QueuedConsoleAppender.createAppender(layout, null, "TestLogFile", "SYSTEM_OUT", "false", "true");

        appender.start();
        config.addAppender(appender);

        root.addAppender(appender, null, null);
        ctx.updateLoggers();
    }

    @Test
    public void testLogger() throws IOException {
        // Used to randomize message
        UUID uuid = UUID.randomUUID();

        // This message should always be logged
        logger.debug("Before shutdown " + uuid);

        // Shutdown Log4j
        QueuedConsoleAppender.setRunning(false);

        // This message should never be logged
        logger.debug("After shutdown " + uuid);

        // Get console appender queue
        BlockingQueue<String> queue = QueuedConsoleAppender.getOutputQueue();

        // Assert size
        assertEquals("Output queue should only contain one message", 1, queue.size());

        // Assert message
        assertEquals("Queued message should be equals to the first message", "[DEBUG] Before shutdown " + uuid + LINE_SEPARATOR, queue.peek());
    }
}
