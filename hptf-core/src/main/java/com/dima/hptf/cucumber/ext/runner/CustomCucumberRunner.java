package com.dima.hptf.cucumber.ext.runner;

import cucumber.api.junit.Cucumber;
import cucumber.runtime.junit.FeatureRunner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.io.IOException;


public class CustomCucumberRunner extends Cucumber{
    private static ExecutionListener listener;

    public CustomCucumberRunner(Class clazz) throws InitializationError, IOException {
       super(clazz);
    }

    protected void runChild(FeatureRunner child, RunNotifier notifier){
        if (listener == null) {
            listener = new ExecutionListener();
            notifier.addListener(listener);
        }
        child.run(notifier);
    }
}
