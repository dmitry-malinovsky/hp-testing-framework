package com.dima.hptf.cucumber.ext.runner;

import com.dima.hptf.cucumber.ext.logging.TestLogHelper;
import gherkin.formatter.model.Scenario;
import org.junit.runner.*;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExecutionListener extends RunListener{
    private Logger logger = LoggerFactory.getLogger(ExecutionListener.class);
    private boolean passed;

    public ExecutionListener() {
    }

    public void testRunStarted(Description description) {
        this.logger.info("Test run started:  " + description.toString());
    }

    public void testStarted(Description description) throws Exception {
        if (description.isTest()) {
            this.logger.info("[ STEP STARTED     ]: " + description.toString());
        } else {
            Field privateSerializableField = Description.class.getDeclaredField("fUniqueId");
            privateSerializableField.setAccessible(true);
            Serializable scenarioCandidate = (Serializable) privateSerializableField.get(description);
            Scenario scenario = (Scenario) scenarioCandidate;
            String innerScenario = scenario.getName();
            int line = scenario.getLine().intValue();
            TestLogHelper.stopTestLogging();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
            TestLogHelper.startTestLogging(format.format(Long.valueOf((new Date()).getTime())) + "_" + innerScenario + "_" + line);
            this.logger.info("[ TEST STARTED     ]: " + description);
            this.logger.info("\r\n");
        }
        this.passed = true;
    }

    public void testFinished(Description description) throws Exception {
        if (this.passed) {
            if (description.isTest()) {
                this.logger.info("[ STEP PASSED     ]: " + description.toString());
            } else {
                this.logger.info("[ TEST PASSED     ]: " + description.toString());
            }
            this.logger.info("\r\b");
        }
    }

    public void testFailure(Failure failure) throws Exception {
        if (failure.getDescription().isTest()) {
            this.logger.info("[ STEP FAILED     ]: " + failure.getDescription().toString());
        } else {
            this.logger.info("[ TEST FAILED     ]: " + failure.getDescription().toString());
            if (failure.getException() instanceof AssertionError) {
                this.logger.info("[ ASSERTION FAILED     ]: " + failure.getException().getMessage() + "\n");
            } else {
                this.logger.info("[ EXCEPTION THROWN     ]: " + failure.getException().getMessage() + "\n");
            }
        }
        this.passed = false;
    }

    public void testIgnored(Description description) throws Exception {
        if (description.isTest()) {
            this.logger.debug("[ STEP IGNORED     ]: " + description.toString());
        } else {
            this.logger.debug("[ TEST IGNORED     ]: " + description.toString());
        }
    }
}
