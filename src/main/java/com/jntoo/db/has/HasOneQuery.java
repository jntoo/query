package com.jntoo.db.has;

import java.lang.reflect.Field;

public class HasOneQuery extends HasQuery
{
    public HasOneQuery() {
    }

    public HasOneQuery(Class<?> target) {
        super(target);
    }

    public HasOneQuery(Class<?> target, String foreignKey) {
        super(target, foreignKey);
    }

    public HasOneQuery(Class<?> target, String foreignKey, String localKey) {
        super(target, foreignKey, localKey);
    }

    public HasOneQuery(Class<?> target, String foreignKey, String localKey, String field) {
        super(target, foreignKey, localKey, field);
    }
}
