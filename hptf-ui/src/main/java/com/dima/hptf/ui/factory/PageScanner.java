package com.dima.hptf.ui.factory;

import com.dima.exceptions.HPTFException;

import com.dima.hptf.ui.annotations.Locator;
import com.dima.hptf.ui.annotations.PageAccessor;
import com.dima.hptf.ui.element.AbstractComponent;
import com.dima.hptf.ui.element.Module;

import com.dima.hptf.ui.pages.AbstractPage;
import com.dima.hptf.ui.pages.Page;
import com.dima.hptf.ui.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;


import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PageScanner {
    private static String PAGES_PACKAGE;

    public PageScanner() {
    }

    @Autowired
    @Qualifier("pageObjectsPackage")
    public void setPagesPackage(String pagesPackage) {
        PAGES_PACKAGE = pagesPackage;
    }

    private static Predicate<Class<?>> isPageWithName(String name) {
        return (clazz) -> {
            return ((PageAccessor) clazz.getDeclaredAnnotation(PageAccessor.class)).name().equals(name);
        };
    }

    private static Predicate<Class<?>> isPageWithUrl(String baseUrl, String url) {
        return (clazz) -> {
            return url.startsWith(baseUrl + ((PageAccessor) clazz.getDeclaredAnnotation(PageAccessor.class)).url());
        };
    }

    private static Predicate<Field> isElementWithName(String name) {
        return (field) -> {
            return ((Locator) field.getAnnotation(Locator.class)).name().equals(name);
        };
    }

    private static Supplier pageWithNameNotFound(String name) {
        return () -> {
            return new HPTFException("Could not find page with name[" + name + "]");
        };
    }

    private static Supplier pageWithUrlNotFound(String url) {
        return () -> {
            return new HPTFException("Could not find page with url[" + url + "]");
        };
    }

    public static Class<? extends AbstractPage> getPageByName(String name) throws Throwable {
        ConfigurationBuilder builder = (new ConfigurationBuilder()).setUrls(ClasspathHelper.forPackage(PAGES_PACKAGE, new ClassLoader[0])).setScanners(new Scanner[]{new TypeAnnotationsScanner(), new SubTypesScanner()});
        Reflections reflections = new Reflections(builder);
        Set typesAnnotatedWith = reflections.getTypesAnnotatedWith(PageAccessor.class);
        Optional first = typesAnnotatedWith.stream().filter(isPageWithName(name)).findFirst();
        return (Class) first.orElseThrow(pageWithNameNotFound(name));
    }

    public static Class<? extends com.gargoylesoftware.htmlunit.AbstractPage> getPageByUrl(String baseUrl, String url) throws Throwable {
        ConfigurationBuilder builder = (new ConfigurationBuilder()).setUrls(ClasspathHelper.forPackage(PAGES_PACKAGE, new ClassLoader[0])).setScanners(new Scanner[]{new TypeAnnotationsScanner(), new SubTypesScanner()});
        Reflections reflections = new Reflections(builder);
        Set typesAnnotatedWith = reflections.getTypesAnnotatedWith(PageAccessor.class);
        Optional first = typesAnnotatedWith.stream().filter(isPageWithUrl(baseUrl, url)).findFirst();
        return (Class) first.orElseThrow(pageWithUrlNotFound(url));
    }

    public static <T extends AbstractComponent> T getPageElementByName(String name, Page page) throws HPTFException {
        try {
            AbstractComponent e = null;
            List fields = ReflectionUtils.extractFieldsByPredicate(page.getClass(), ComponentCreator.isElement());
            Iterator var4 = fields.iterator();

            while (var4.hasNext()) {
                Field field = (Field) var4.next();
                field.setAccessible(true);
                Locator locator = (Locator) field.getDeclaredAnnotation(Locator.class);
                if (locator.name().equals(name)) {
                    e = (AbstractComponent) field.get(page);
                    break;
                }

                if (Arrays.asList(field.getType().getInterfaces()).contains(Module.class)) {
                    Module module = (Module) field.get(page);
                    e = getModuleElementByName(name, module);
                    if (e != null) {
                        break;
                    }
                }
            }

            return e;
        } catch (IllegalAccessException var8) {
            throw new HPTFException("Failed to get element with name[" + name + "] from page[" + page.name() + "]", var8);
        }
    }

    public static <T extends AbstractComponent> T getModuleElementByName(String name, Module module) throws HPTFException {
        AbstractComponent t = null;

        try {
            List e = ReflectionUtils.extractFieldsByPredicate(module.getClass(), ComponentCreator.isElement());
            Iterator var4 = e.iterator();

            while (var4.hasNext()) {
                Field field = (Field) var4.next();
                field.setAccessible(true);
                Locator locator = (Locator) field.getDeclaredAnnotation(Locator.class);
                if (locator.name().equals(name)) {
                    t = (AbstractComponent) field.get(module);
                    break;
                }

                if (Arrays.asList(field.getType().getInterfaces()).contains(Module.class)) {
                    Module m = (Module) field.get(module);
                    t = getModuleElementByName(name, m);
                    if (t != null) {
                        break;
                    }
                }
            }

            return t;
        } catch (IllegalAccessException var8) {
            throw new HPTFException("Failed to retrieve component with name[" + name + "] from module[" + module.getName() + "]", var8);
        }
    }
}
