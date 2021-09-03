package com.jntoo.db.annotation;

import java.lang.annotation.*;
import java.util.Map;

@Inherited
@Documented
@Target(ElementType.FIELD )
@Retention(RetentionPolicy.RUNTIME)
public @interface Fields {
    String value() default "";
    FieldType type() default FieldType.DEFAULT;

    boolean autoUpdateTime() default false;
    boolean autoInsertTime() default false;
    String autoUpdate() default "";
    String autoInsert() default "";
}
