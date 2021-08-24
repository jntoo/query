package com.jntoo.db.model;

import com.jntoo.db.annotation.Fields;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FieldInfoModel {
    private String name;

    private Field field;
    private Fields annField;

    private Method setMethod;
    private Method getMethod;

    private Method autoMethodUpdate;
    private String autoMethodUpdateString;
    private Method autoMethodInsert;
    private String autoMethodInsertString;

    public String getName() {
        return name;
    }

    public Field getField() {
        return field;
    }

    public Fields getAnnField() {
        return annField;
    }

    public Method getSetMethod() {
        return setMethod;
    }

    public Method getGetMethod() {
        return getMethod;
    }

    public Method getAutoMethodUpdate() {
        return autoMethodUpdate;
    }

    public String getAutoMethodUpdateString() {
        return autoMethodUpdateString;
    }

    public Method getAutoMethodInsert() {
        return autoMethodInsert;
    }

    public String getAutoMethodInsertString() {
        return autoMethodInsertString;
    }
}
