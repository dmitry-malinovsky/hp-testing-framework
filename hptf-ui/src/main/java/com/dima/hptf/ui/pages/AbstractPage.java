package com.dima.hptf.ui.pages;

import com.dima.hptf.ui.browser.Browser;


public abstract class AbstractPage implements Page {
    protected Browser browser;
    private String name;
    private String url;

    public AbstractPage(Browser browser, String name, String url) {
        this.browser = browser;
        this.name = name;
        this.url = url;
    }

    public void open() {
        this.browser.goToUrl(this.url);
    }

    public String getUrl() {
        return this.url;
    }

    public String name() {
        return this.name();
    }

    public String title() {
        return this.title();
    }

    public boolean isCurrentPage() {
        return this.browser.getDriver().getCurrentUrl().startsWith(this.url);
    }

    public String toString() {
        return "AbstractPage[url=\'" + this.url + '\'' + ", name= \'" + this.name + '\'' + ']';
    }
}
