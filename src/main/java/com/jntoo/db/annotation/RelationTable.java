package com.jntoo.db.annotation;

import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RelationTable {
    /**
     * 目标实体表
     * @return Pojo 类型
     */
    Class<?> value();
}
