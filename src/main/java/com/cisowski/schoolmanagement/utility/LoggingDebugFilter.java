package com.cisowski.schoolmanagement.utility;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class LoggingDebugFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent iLoggingEvent) {
        if(iLoggingEvent.getLevel().toInt() == Level.DEBUG_INT){
            if(iLoggingEvent.getMessage().startsWith(DbLogger.DEBUG_MARKER)){
                return FilterReply.ACCEPT;
            }
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }
}
