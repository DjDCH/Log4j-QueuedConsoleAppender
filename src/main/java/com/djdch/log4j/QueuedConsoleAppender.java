package com.djdch.log4j;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Booleans;

/**
 * Inspired by ConsoleAppender from log4j.
 * @see org.apache.logging.log4j.core.appender.ConsoleAppender
 */
@Plugin(name="QueuedConsole", category="Core", elementType="appender", printObject=true)
public final class QueuedConsoleAppender extends AbstractAppender {

    private static final BlockingQueue<String> output = new LinkedBlockingQueue<>();

    private static volatile boolean running = true;

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();

    protected QueuedConsoleAppender(final String name, final Layout<? extends Serializable> layout, final Filter filter, final boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @PluginFactory
    public static QueuedConsoleAppender createAppender(
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("name") String name,
            @PluginAttribute(value = "target", defaultString = "SYSTEM_OUT") final String targetStr, // XXX: Ignored
            @PluginAttribute(value = "follow", defaultBoolean = false) final String follow,          // XXX: Ignored
            @PluginAttribute(value = "ignoreExceptions", defaultBoolean = true) final String ignore) {
        if (name == null) {
            LOGGER.error("No name provided for QueuedConsoleAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
        return new QueuedConsoleAppender(name, layout, filter, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {
        readLock.lock();
        try {
            final byte[] bytes = getLayout().toByteArray(event);
            if (isRunning()) {
                output.add(new String(bytes)); // FIXME: Optimize by removing unnecessary String object
            } else {
                System.out.write(bytes);
                System.out.flush();
            }
        } catch (Exception ex) {
            if (!ignoreExceptions()) {
                throw new AppenderLoggingException(ex);
            }
        } finally {
            readLock.unlock();
        }
    }

    public static BlockingQueue<String> getOutputQueue() {
        return output;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean isRunning) {
        running = isRunning;
    }
}
