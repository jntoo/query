package com.jntoo.db.model;

import java.util.HashMap;

public class WhereModel {
    private String name;
    private String exp;
    private Object value;
    private String connect;
    private boolean raw = false;
    public WhereModel(){

    }

    public WhereModel(String raw)
    {
        this.name = raw;
        this.raw = true;
    }

    public WhereModel(String name , Object value){
        this(name , null , value,null);
    }
    public WhereModel(String name , String exp , Object value)
    {
        this(name , null , value,null);
    }
    public WhereModel(String name , String exp, Object value , String connect){
        this.name = name;
        this.exp = exp == null ? "=" : exp;
        this.value = value;
        this.connect = connect == null ? "and" : connect;
    }

    public boolean isRaw() {
        return raw;
    }

    public void setRaw(boolean raw) {
        this.raw = raw;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getExp() {
        return exp;
    }
    public void setExp(String exp) {
        this.exp = exp;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public String getConnect() {
        return connect;
    }
    public void setConnect(String connect) {
        this.connect = connect;
    }
}
