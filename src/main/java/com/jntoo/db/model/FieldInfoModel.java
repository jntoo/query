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

    public void setName(String name) {
        this.name = name;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Fields getAnnField() {
        return annField;
    }

    public void setAnnField(Fields annField) {
        this.annField = annField;
    }

    public Method getSetMethod() {
        return setMethod;
    }

    public void setSetMethod(Method setMethod) {
        this.setMethod = setMethod;
    }

    public Method getGetMethod() {
        return getMethod;
    }

    public void setGetMethod(Method getMethod) {
        this.getMethod = getMethod;
    }

    public Method getAutoMethodUpdate() {
        return autoMethodUpdate;
    }

    public void setAutoMethodUpdate(Method autoMethodUpdate) {
        this.autoMethodUpdate = autoMethodUpdate;
    }

    public String getAutoMethodUpdateString() {
        return autoMethodUpdateString;
    }

    public void setAutoMethodUpdateString(String autoMethodUpdateString) {
        this.autoMethodUpdateString = autoMethodUpdateString;
    }

    public Method getAutoMethodInsert() {
        return autoMethodInsert;
    }

    public void setAutoMethodInsert(Method autoMethodInsert) {
        this.autoMethodInsert = autoMethodInsert;
    }

    public String getAutoMethodInsertString() {
        return autoMethodInsertString;
    }

    public void setAutoMethodInsertString(String autoMethodInsertString) {
        this.autoMethodInsertString = autoMethodInsertString;
    }


    /*public String getName() {
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
    }*/



}
