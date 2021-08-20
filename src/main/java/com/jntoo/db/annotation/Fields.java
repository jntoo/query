package com.jntoo.db.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

@Inherited
@Documented
@Target(ElementType.FIELD)
public @interface Fields {
    String value() default "";
    FieldType type() default FieldType.DEFAULT;

}
