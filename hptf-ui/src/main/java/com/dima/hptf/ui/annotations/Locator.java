package com.dima.hptf.ui.annotations;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Locator {
    String name();

    String xpath() default "";
}
