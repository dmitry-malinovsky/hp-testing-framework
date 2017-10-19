package com.dima.hptf.ui.browser;

import com.google.common.base.Function;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by dmalinovschi on 12/28/2016.
 */
public class Browser {
    private WebDriver driver;
    private long timeout;
    private long step;

    public Browser(WebDriver driver, long timeout, long step) {
        this.driver = driver;
        this.timeout = timeout;
        this.step = step;
    }

    public void goToUrl(String url) {
        this.driver.manage().window().maximize();
        this.driver.navigate().to(url);
    }

    public void goBack() {
        this.driver.navigate().back();
    }

    public WebElement findElement(final String xpath) {
        return (WebElement) (new FluentWait(this.driver)).withTimeout(this.timeout, TimeUnit.SECONDS)
                .pollingEvery(this.step, TimeUnit.SECONDS).ignoring(WebDriverException.class).until((Function) (webDriver) -> {
                    return this.driver.findElement(By.xpath(xpath));
                });
    }

    public List<WebElement> findElements(final String xpath) {
        return (List) (new FluentWait(this.driver)).withTimeout(this.timeout, TimeUnit.SECONDS)
                .pollingEvery(this.step, TimeUnit.SECONDS).ignoring(WebDriverException.class).until((Function) (webDriver) -> {
                    return this.driver.findElements(By.xpath(xpath));
                });
    }

    public String getPageTitle() {
        return this.driver.getTitle();
    }

    public void close() {
        if (this.driver != null) {
            this.driver.close();
        }
    }

    public void quit() {
        if (this.driver != null) {
            this.driver.quit();
        }
    }

    public WebDriver getDriver() { return this.driver;}

    public void refresh(){ this.driver.navigate().refresh();}

    public void executeJavaScript(String script, String... xpaths){
        ArrayList elements = new ArrayList();
        String[] var4 = xpaths;
        int var5 = xpaths.length;

        for (int var6 = 0; var6< var5; ++var6){
            String xpath = var4[var6];
            elements.add(By.xpath(xpath).findElement(this.driver));
        }
        ((JavascriptExecutor)this.driver).executeScript(script, elements.toArray(new WebElement[0]));
    }

    public void executeJavaScript(String script, WebElement... elements){
        ((JavascriptExecutor)this.driver).executeScript(script, elements);
    }

    public String getCurrentUrl(){ return this.driver.getCurrentUrl();}

    public void waitForPageLoaded(){
        ExpectedCondition expectation = (driver1) -> {
            return ((JavascriptExecutor) driver1).executeScript(" return document.readystate", new Object[0]).equals("complete");
        };
        FluentWait wait = (new FluentWait(this.driver)).withTimeout(this.timeout, TimeUnit.SECONDS).pollingEvery(this.step, TimeUnit.SECONDS).ignoring(WebDriverException.class);

        try {
            wait.until(expectation);
        } catch (Throwable var4){
            Assert.assertFalse("Timeout for PageLoadRequest to complete. ", true);
        }
    }
}
