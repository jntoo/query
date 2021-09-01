package com.jntoo.db.annotation;

import com.jntoo.db.QueryWrapper;

import java.lang.annotation.*;
import java.lang.reflect.Method;

@Inherited
@Documented
@Target(ElementType.FIELD )
@Retention(RetentionPolicy.RUNTIME)
public @interface OneToOne {
    /**
     * 关联表得实体类
     * @return 实体类
     */
    Class<?> value();

    /**
     * 关联得外键字段
     */
    String foreignKey();

    /**
     * 目标表得关联键
     */
    String localKey();


}

