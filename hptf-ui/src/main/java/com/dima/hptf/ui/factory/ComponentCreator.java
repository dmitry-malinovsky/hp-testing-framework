package com.dima.hptf.ui.factory;

import com.dima.hptf.ui.annotations.Locator;
import com.dima.hptf.ui.browser.Browser;
import com.dima.hptf.ui.element.AbstractComponent;
import com.dima.hptf.ui.element.Component;
import com.dima.hptf.ui.element.impl.GroupComponent;
import com.dima.hptf.ui.utils.ReflectionUtils;
import com.sun.xml.internal.ws.api.server.Module;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by dmalinovschi on 12/27/2016.
 */
public class ComponentCreator {
    private ComponentCreator() {
    }

    public static Predicate<Field> isElement() {
        return (field) -> {
            return AbstractComponent.class.isAssignableFrom(field.getType());
        };
    }

    public static AbstractComponent createComponent(Browser browser, Field field, Component parent) throws Exception {
        Locator annotation = (Locator)field.getAnnotation(Locator.class);
        Class elementType = field.getType();
        Object parentObject = parent != null?parent:Component.class;
        AbstractComponent component = createInstance(elementType, field, new Object[]{browser, annotation.name(), parentObject, annotation.xpath()});
        if(Arrays.asList(field.getType().getInterfaces()).contains(Module.class)) {
            createContent(browser, component, component);
        }

        return component;
    }

    public static <T> T createContent(Browser browser, T parent, Component parentComponent) throws Exception {
        try {
            List e = ReflectionUtils.extractFieldsByPredicate(parent.getClass(), isElement());
            Iterator var4 = e.iterator();

            while(var4.hasNext()) {
                Field field = (Field)var4.next();
                field.setAccessible(true);
                AbstractComponent abstractComponent = createComponent(browser, field, parentComponent);
                field.set(parent, abstractComponent);
            }

            return parent;
        } catch (IllegalAccessException var7) {
            throw new Exception("Failed to create content for entity[" + parent.getClass().getSimpleName() + "]", var7);
        }
    }

    public static <T extends AbstractComponent> T createInstance(Class<T> type, Field field, Object... parameters) throws Exception {
        try {
            Object[] e = parameters;
            if(type.equals(GroupComponent.class)) {
                Class clazz = (Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                ArrayList objects = new ArrayList(Arrays.asList(parameters));
                objects.add(clazz);
                e = objects.toArray();
            }

            return (AbstractComponent)ReflectionUtils.newInstance(type, e);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException var6) {
            throw new Exception("Failed to create instance of type[" + type.getName() + "] for field[" + field.getName() + "]", var6);
        }
    }

}
