package com.dima.hptf.ui.pages;

public interface Page {
    void open();

    String getUrl();

    String name();

    String title();

    boolean isCurrentPage();
}
