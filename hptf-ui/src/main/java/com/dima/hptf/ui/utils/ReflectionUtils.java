package com.dima.hptf.ui.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class ReflectionUtils {
    public ReflectionUtils() {
    }

    public static <T> T newInstance(Class<T> type, Object... parameters) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, InvocationTargetException {
        Constructor constructor = type.getDeclaredConstructors()[0];
        Object[] objects = ((List) Arrays.asList(parameters).stream().map((o) -> {
            return o instanceof Class && ((Class)o).getSimpleName().equals("Component")?null:o;
        }).collect(Collectors.toList())).toArray();
        return type.cast(constructor.newInstance(objects));
    }

    public static List<Field> extractFieldsByPredicate(Class<?> type, Predicate<Field> predicate) {
        List declaredFields = Arrays.asList(getAllFields(type));
        return (List)declaredFields.stream().filter(predicate).collect(Collectors.toList());
    }

    public static Field[] getAllFields(Class<?> clazz) {
        List classes = getAllSuperclasses(clazz);
        classes.add(clazz);
        return getAllFields(classes);
    }

    private static Field[] getAllFields(List<Class<?>> classes) {
        HashSet fields = new HashSet();
        Iterator var2 = classes.iterator();

        while(var2.hasNext()) {
            Class clazz = (Class)var2.next();
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }

        return (Field[])fields.toArray(new Field[fields.size()]);
    }

    public static List<Class<?>> getAllSuperclasses(Class<?> clazz) {
        ArrayList classes = new ArrayList();

        for(Class superclass = clazz.getSuperclass(); superclass != null; superclass = superclass.getSuperclass()) {
            classes.add(superclass);
        }

        return classes;
    }

}
