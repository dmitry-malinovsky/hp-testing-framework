package com.dima.hptf.ui.action.impl;


import com.dima.hptf.ui.browser.Browser;
import com.dima.hptf.ui.element.AbstractComponent;
import com.dima.hptf.ui.utils.ScreenshotUtils;
import com.dima.hptf.cucumber.ext.logging.TestLogHelper;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Supplier;

public class AbstractAction {
    private static int screenshotIndex = 1;
    private static String currentLogName = "";
    private final int attemptsNumber = 3;
    protected Browser browser;
    protected AbstractComponent element;
    private Logger logger = LoggerFactory.getLogger(AbstractAction.class);

    public AbstractAction(Browser browser, AbstractComponent element) {
        this.browser = browser;
        this.element = element;
    }

    protected void execute(Runnable action) {
        String folderName = ScreenshotUtils.getCurrentScreenshotFolderPath();
        if(ScreenshotUtils.makeFullScreenshot().booleanValue()) {
            this.updateScreenshotIndexIfNewTest();

            try {
                ScreenshotUtils.highlightElement(this.browser, this.element.find());
            } catch (TimeoutException var6) {
                this.logger.trace("Timeout for highlight element", var6);
            }

            this.takeScreenshotBefore(folderName);
        }

        for(int te = 0; te < 3; ++te) {
            try {
                action.run();
                break;
            } catch (StaleElementReferenceException var7) {
                this.logger.error("Runnable action execution failed on attempt " + te, var7);
            } catch (InvalidElementStateException var8) {
                this.logger.error("Runnable action execution failed on attempt " + te, var8);
            }
        }

        if(ScreenshotUtils.makeFullScreenshot().booleanValue()) {
            this.takeScreenshotAfter(folderName);

            try {
                ScreenshotUtils.unhighlightElement(this.browser, this.element.find());
            } catch (TimeoutException var5) {
                this.logger.trace("Timeout for highlight element", var5);
            }
        }

    }

    protected <T> T execute(Supplier<T> action) {
        String folderName = ScreenshotUtils.getCurrentScreenshotFolderPath();
        if(ScreenshotUtils.makeFullScreenshot().booleanValue()) {
            this.updateScreenshotIndexIfNewTest();
        }

        Object result = null;

        for(int te = 0; te < 3; ++te) {
            try {
                result = action.get();
                break;
            } catch (StaleElementReferenceException var8) {
                this.logger.error("Supplier action execution failed on attempt " + te, var8);
            } catch (InvalidElementStateException var9) {
                this.logger.error("Runnable action execution failed on attempt " + te, var9);
            }
        }

        if(ScreenshotUtils.makeFullScreenshot().booleanValue()) {
            try {
                ScreenshotUtils.highlightElement(this.browser, this.element.find());
            } catch (TimeoutException var7) {
                this.logger.trace("Timeout for highlight element", var7);
            }

            this.takeScreenshotAfter(folderName);

            try {
                ScreenshotUtils.unhighlightElement(this.browser, this.element.find());
            } catch (TimeoutException var6) {
                this.logger.trace("Timeout for highlight element", var6);
            }
        }

        return (T) result;
    }

    private void updateScreenshotIndexIfNewTest() {
        if(!currentLogName.equals(TestLogHelper.getCurrentLogName())) {
            currentLogName = TestLogHelper.getCurrentLogName();
            screenshotIndex = 1;
        }

    }

    private void takeScreenshotBefore(String folderName) {
        this.takeScreenshot(folderName, "before ");
    }

    private void takeScreenshotAfter(String folderName) {
        this.takeScreenshot(folderName, "after ");
    }

    private void takeScreenshot(String folderName, String beforeOrAfter) {
        try {
            ScreenshotUtils.makeAScreenshot(this.browser.getDriver(), folderName, beforeOrAfter + this.element.getName());
        } catch (IOException var4) {
            this.logger.error("Could not save screenshot", var4);
        }

    }

}
