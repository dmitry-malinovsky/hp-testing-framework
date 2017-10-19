package com.dima.hptf.ui.annotations;

import java.lang.annotation.*;

/**
 * Created by dmalinovschi on 12/28/2016.
 */


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented

public @interface PageAccessor {
    String name() default "";

    String url() default "/";
}
