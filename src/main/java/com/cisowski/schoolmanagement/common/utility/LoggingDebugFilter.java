package com.cisowski.schoolmanagement.common.utility;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class LoggingDebugFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent iLoggingEvent) {
        if(iLoggingEvent.getLevel().toInt() == Level.INFO_INT){
            if(iLoggingEvent.getMessage().startsWith(DbLogger.INFO_LOG_MARKER)){
                return FilterReply.ACCEPT;
            }
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }
}
