package com.jntoo.db.annotation;

import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String value() default "";
    String prefix() default "";
    boolean sysPrefix() default true;
}
