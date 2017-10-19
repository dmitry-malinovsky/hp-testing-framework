package com.dima.hptf.ui.element.impl;

import com.dima.hptf.ui.browser.Browser;
import com.dima.hptf.ui.element.AbstractComponent;
import com.dima.hptf.ui.element.Component;
import com.dima.hptf.ui.factory.ComponentCreator;
import com.dima.hptf.ui.utils.ReflectionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class GroupComponent<T extends AbstractComponent> extends AbstractComponent {
    private Class<T> type;

    public GroupComponent(Browser browser, String name, Component parent, String xpath, Class<T> type) {
        super(browser, name, parent, xpath);
        this.type = type;
    }

    public Class<T> getType() {
        return this.type;
    }

    public int getSize() {
        return this.findAll().size();
    }

    public List<T> getAllElements() throws Exception {
        ArrayList all = new ArrayList();
        int size = this.getSize();
        String xpath = "%s[%s]";

        for(int i = 1; i <= size; ++i) {
            all.add(this.createChildInstance(this.type, String.format(xpath, new Object[]{this.getXpath(), Integer.valueOf(i)})));
        }

        return all;
    }

    public T getElementByIndex(int index) throws Exception {
        String xpath = String.format("%s[%s]", new Object[]{this.getXpath(), Integer.valueOf(index)});
        return this.createChildInstance(this.type, xpath);
    }

    public T getElementByText(String text) throws Exception {
        String childTextXpath = String.format("[text()=\'%s\' or .//*[text()=\'%s\']]", new Object[]{text, text});
        return this.createChildInstance(this.type, this.getXpath() + childTextXpath);
    }

    public T getElementByChildAttribute(String attribute, String value) throws Exception {
        String attributeXpath = String.format("[.//*[@%s=\'%s\']]", new Object[]{attribute, value});
        return this.createChildInstance(this.type, this.getXpath() + attributeXpath);
    }

    public T getElementByAttribute(String attribute, String value) throws Exception {
        String attributeXpath = String.format("[@%s=\'%s\']", new Object[]{attribute, value});
        return this.createChildInstance(this.type, this.getXpath() + attributeXpath);
    }

    private T createChildInstance(Class<T> type, String xpath) throws Exception {
        try {
            AbstractComponent e;
            if(type.equals(GroupComponent.class)) {
                e = this.createChildInstance(type, xpath);
            } else {
                e = (AbstractComponent) ReflectionUtils.newInstance(type, new Object[]{this.getBrowser(), this.getName(), this.getParent(), this.getXpath()});
            }

            ComponentCreator.createContent(this.getBrowser(), e, e);
            e.setXpath(xpath);
            return e;
        } catch (Exception var4) {
            throw new Exception("Failed to clone object[" + type + "]", var4);
        }
    }

    public T getElementByPartialText(String text) throws Exception {
        String childTextXpath = String.format("[contains(text(),\'%s\') or .//*[contains(text(),\'%s\')]]", new Object[]{text, text});
        return this.createChildInstance(this.type, this.getXpath() + childTextXpath);
    }

    public WebElement container() {
        return this.find().findElement(By.xpath(".."));
    }
}

