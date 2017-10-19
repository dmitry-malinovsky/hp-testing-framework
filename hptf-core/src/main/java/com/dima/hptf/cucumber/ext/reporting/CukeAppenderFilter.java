package com.dima.hptf.cucumber.ext.reporting;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class CukeAppenderFilter extends Filter<ILoggingEvent> {
    public CukeAppenderFilter() {
    }

    public FilterReply decide(ILoggingEvent event) {
        return event.getLoggerName().contains("ExecutionListener") ? FilterReply.DENY : FilterReply.ACCEPT;
    }
}
