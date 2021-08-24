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

    /**
     * 更新时字段插入字段信息
     */
    boolean autoUpdateTime() default false;
    /**
     * 插入时字段插入字段信息
     */
    boolean autoInsertTime() default false;
    String autoUpdate() default "";
    String autoInsert() default "";
}
