package com.dima.hptf.cucumber.ext.reporting;


import cucumber.api.Scenario;

/**
 * Created by dmalinovschi on 8/26/2017.
 */
public class CukeScenarioContext {
    private static CukeScenarioContext instance;
    private Scenario scenario;

    public static synchronized CukeScenarioContext getInstance() {
        if (instance == null) {
            instance = new CukeScenarioContext();
        }
        return instance;
    }

    private CukeScenarioContext() {
    }

    public Scenario getScenario() {
        return this.scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public void attachScreenShot(byte[] screenshot) {
        this.scenario.embed(screenshot, "image/png");
    }
}
