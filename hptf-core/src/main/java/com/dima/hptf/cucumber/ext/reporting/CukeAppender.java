package com.dima.hptf.cucumber.ext.reporting;

import ch.qos.logback.core.AppenderBase;

/**
 * Created by dmalinovschi on 8/26/2017.
 */
public class CukeAppender extends AppenderBase {
    public CukeAppender() {
    }

    protected void append(Object o) {
        CukeScenarioContext.getInstance().getScenario().write(o.toString());
    }
}
