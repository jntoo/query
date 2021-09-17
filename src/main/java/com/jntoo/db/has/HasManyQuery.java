package com.jntoo.db.has;

public class HasManyQuery extends HasQuery{
    public HasManyQuery() {
    }

    public HasManyQuery(Class<?> target) {
        super(target);
    }

    public HasManyQuery(Class<?> target, String foreignKey) {
        super(target, foreignKey);
    }

    public HasManyQuery(Class<?> target, String foreignKey, String localKey) {
        super(target, foreignKey, localKey);
    }

    public HasManyQuery(Class<?> target, String foreignKey, String localKey, String field) {
        super(target, foreignKey, localKey, field);
    }
}
