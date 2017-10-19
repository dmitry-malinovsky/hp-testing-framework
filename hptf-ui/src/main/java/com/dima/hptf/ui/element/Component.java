package com.dima.hptf.ui.element;

import com.dima.hptf.ui.browser.Browser;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface Component {
    String getXpath();

    Component getParent();

    WebElement find();

    Browser getBrowser();

    List<WebElement> findAll();

    String getText();

    String getValue();

    String getFullXpath();

    default boolean isReady() {
        return true;
    }

}
