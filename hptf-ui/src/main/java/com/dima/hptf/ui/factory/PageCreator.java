package com.dima.hptf.ui.factory;

import com.dima.exceptions.HPTFException;
import com.dima.hptf.ui.annotations.PageAccessor;
import com.dima.hptf.ui.browser.Browser;
import com.dima.hptf.ui.element.Component;
import com.dima.hptf.ui.pages.AbstractPage;
import com.dima.hptf.ui.pages.Page;
import com.dima.hptf.ui.utils.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;

public class PageCreator {
    private final Browser browser;
    private final String baseUr;
    private Map<Class<? extends AbstractPage>, Page> cache;

    public PageCreator(Browser browser, String baseUr){
        this.browser = browser;
        this.baseUr = baseUr;
        this.cache = new HashMap();
    }

    public <T extends AbstractPage> T createPage(String pageName) throws Throwable {
        Class page = PageScanner.getPageByName(pageName);
        return this.createPage(page);
    }

    public <T extends AbstractPage> T createPage(Class<T> type) throws HPTFException {
        try {
            Page e = this.cache.get(type);
            if (e != null) {
                return (AbstractPage)type.cast(e);
            } else {
                PageAccessor[] pageAnnotations = (PageAccessor[]) type.getDeclaredAnnotationsByType(PageAccessor.class);
                if (pageAnnotations.length == 0) {
                    throw new HPTFException("Clas of type { " + type + " } is not a page object. \r\n Missing annotations.");
                } else {
                    PageAccessor pageAccessor = pageAnnotations[0];
                    String url = this.baseUr + pageAccessor.url();
                    AbstractPage t = (AbstractPage) ReflectionUtils.newInstance(type,
                            new Object[]{this.browser, url, pageAccessor.name()});
                    ComponentCreator.createContent(this.browser, t, (Component) null);
                    this.cache.put(t.getClass(), t);
                    return t;
                }
            }
        } catch (Exception var7) {
            throw new HPTFException("Failed to create page of type [" + type.getSimpleName() + "]", var7);
        }
    }

}
