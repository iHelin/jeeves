package me.ianhe.jeeves.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * @author Ian He
 * @since 2018/8/17 14:09
 */
public class MyAppender extends AppenderBase<LoggingEvent> {

    @Override
    protected void append(LoggingEvent event) {
        if (Level.ERROR.equals(event.getLevel())) {
        }
    }

}
