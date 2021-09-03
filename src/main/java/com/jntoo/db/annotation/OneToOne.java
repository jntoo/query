package com.jntoo.db.annotation;

import com.jntoo.db.QueryWrapper;

import java.lang.annotation.*;
import java.lang.reflect.Method;

@Inherited
@Documented
@Target(ElementType.FIELD )
@Retention(RetentionPolicy.RUNTIME)
public @interface OneToOne {
    Class<?> value();
    String foreignKey() default "";
    String localKey() default "";
}

