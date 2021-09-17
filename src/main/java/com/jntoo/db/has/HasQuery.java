package com.jntoo.db.has;

import com.jntoo.db.QueryOptions;

import java.lang.reflect.Field;

abstract public class HasQuery extends QueryOptions<HasQuery> {

    protected Class<?> target;
    protected String foreignKey;
    protected String localKey;
    protected String field;

    public HasQuery(){
        reset();
    }

    public HasQuery(Class<?> target)
    {
        reset();
        this.target = target;
    }

    public HasQuery(Class<?> target , String foreignKey)
    {
        reset();
        this.target = target;
        this.foreignKey = foreignKey;
    }

    public HasQuery(Class<?> target, String foreignKey, String localKey) {
        reset();
        this.target = target;
        this.foreignKey = foreignKey;
        this.localKey = localKey;
    }

    public HasQuery(Class<?> target, String foreignKey, String localKey, String field) {
        reset();
        this.target = target;
        this.foreignKey = foreignKey;
        this.localKey = localKey;
        this.field = field;
    }

    public Class<?> getTarget() {
        return target;
    }

    public void setTarget(Class<?> target) {
        this.target = target;
    }

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String getLocalKey() {
        return localKey;
    }

    public void setLocalKey(String localKey) {
        this.localKey = localKey;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
