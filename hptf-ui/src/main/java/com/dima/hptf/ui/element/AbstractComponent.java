package com.dima.hptf.ui.element;

import com.google.common.base.Function;
import com.dima.hptf.ui.browser.Browser;
import com.dima.hptf.ui.element.Component;
import com.dima.hptf.ui.action.impl.IsDisplayedAction;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;


public class AbstractComponent implements Component {
    private Browser browser;
    private Component parent;
    private String xpath;
    private String name;

    public AbstractComponent(Browser browser, String name, Component parent, String xpath) {
        this.parent = parent;
        this.xpath = xpath;
        this.browser = browser;
        this.name = name;
    }

    public String getLocator() {
        return this.xpath;
    }

    public Component getParent() {
        return this.parent;
    }

    public void setParent(Component parent) {
        this.parent = parent;
    }

    public String getXpath() {
        return this.xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getFullXpath() {
        return this.parent != null?this.parent.getFullXpath() + this.xpath:this.xpath;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Browser getBrowser() {
        return this.browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    public WebElement directFind() {
        return this.browser.findElement(this.getFullXpath());
    }

    public WebElement find() {
        if(this.parent != null) {
            FluentWait wait = (new FluentWait(this.parent)).withTimeout(30L, TimeUnit.SECONDS).pollingEvery(1L, TimeUnit.SECONDS);
            wait.until((p) -> {
                return Boolean.valueOf(p.isReady());
            });
        }

        return this.browser.findElement(this.getFullXpath());
    }

    public List<WebElement> findAll() {
        return this.browser.findElements(this.getFullXpath());
    }

    public String getText() {
        return this.find().getText();
    }

    public String getValue() {
        return this.find().getAttribute("value");
    }

    public String getAttributeValue(String attribute) {
        return this.find().getAttribute(attribute);
    }

    public boolean isSelected() {
        return this.find().isSelected();
    }

    public boolean isDisplayed() {
        return (new IsDisplayedAction(this)).execute().booleanValue();
    }


    public boolean isEnabled() {
        return this.find().isEnabled();
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{" + "browser=" + this.getBrowser() + ", parent=" + this.getParent() + ", xpath=\'" + this.getXpath() + '\'' + ", name=\'" + this.getName() + '\'' + '}';
    }

}
