package com.jntoo.db.annotation;

import java.lang.annotation.*;

@Inherited
@Documented
@Target( { ElementType.FIELD } )
@Retention(RetentionPolicy.RUNTIME)
public @interface HasMany {
    String foreignKey() default ""; // 外键关联字段
    String localKey() default "id";   // 目标表的关联字段
    Class<?> target() default void.class;  // 所属类型，不填写则默认字段上的类型


    String[] where() default {};
    String[] field() default {};
    String[] order() default {};

    Class<?> callback() default void.class; // 如果需要一些其他设置则会使用，必须继承HasQueryCallback
}
