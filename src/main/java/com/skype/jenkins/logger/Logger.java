package com.skype.jenkins.logger;

import java.util.List;

import org.slf4j.LoggerFactory;

public class Logger {

    public static Logger out = new Logger(Logger.class.getName()); 

    private final org.slf4j.Logger loggerClass;

    public Logger(final String loggerName) {
        this.loggerClass = LoggerFactory.getLogger(loggerName);
    }

    public void trace(final Object message) {
        loggerClass.trace("{}", message);
    }

    public void trace(final Object message, final Throwable e) {
        loggerClass.trace("{}", message, e);
    }

    public void trace(final Object... objects) {
        loggerClass.trace(objectsAsString(objects));
    }

    public void trace(final String format, final Object... objects) {
        loggerClass.trace(String.format(format, objects));
    }

    public void debug(final Object message) {
        loggerClass.debug("{}", message);
    }

    public void debug(final Object message, final Throwable e) {
        loggerClass.debug("{}", message, e);
    }

    public void debug(final Object... objects) {
        loggerClass.debug(objectsAsString(objects));
    }

    public void debug(final String format, final Object... objects) {
        loggerClass.debug(String.format(format, objects));
    }

    public void info(final Object message) {
        loggerClass.info("{}", message);
    }

    public void info(final Object message, final Throwable e) {
        loggerClass.info("{}", message, e);
    }

    public void info(final Object... objects) {
        loggerClass.info(objectsAsString(objects));
    }

    public void info(final String format, final Object... objects) {
        loggerClass.info(String.format(format, objects));
    }

    public void warn(final Object message) {
        loggerClass.warn("{}", message);
    }

    public void warn(final Object message, final Throwable e) {
        loggerClass.warn("{}", message, e);
    }

    public void warn(final Object... objects) {
        loggerClass.warn(objectsAsString(objects));
    }

    public void warn(final String format, final Object... objects) {
        loggerClass.warn(String.format(format, objects));
    }

    public void error(final Object message) {
        loggerClass.error("{}", message);
    }

    public void error(final Object message, final Throwable e) {
        loggerClass.error("{}", message, e);
    }

    public void error(final Object... objects) {
        loggerClass.error(objectsAsString(objects));
    }

    public void error(final String format, final Object... objects) {
        loggerClass.error(String.format(format, objects));
    }

    private String objectsAsString(final Object... objects) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Object obj : objects) {
            stringBuilder.append((stringBuilder.length() == 0) ? obj : ", " + obj);
        }
        return stringBuilder.toString();
    }

    public void traceErrorMessages(final String loggerMessage, final List<String> errorMessages) {
        StringBuilder sb = new StringBuilder();
        for (String message : errorMessages) {
            sb.append(String.format("[%s]", message));
        }
        Logger.out.trace("%s: [%s]", loggerMessage, sb.toString());
    }

}