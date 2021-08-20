package com.jntoo.db.model;

import java.util.HashMap;



public class QMap<K,V> extends HashMap<K,V> {

    public String getString(Object key)
    {
        V value = get(key);
        if(value == null)return null;
        return String.valueOf(value);
    }

    public double getDouble(Object key)
    {
        V value = get(key);
        if(value == null)return 0;
        double aDouble;
        try {
            aDouble = Double.parseDouble(value.toString());
        }catch (NumberFormatException e){
            return 0d;
        }
        return aDouble;
    }

    public long getLong(Object key)
    {
        V value = get(key);
        if(value == null)return 0;
        long aDouble;
        try {
            aDouble = Long.parseLong(value.toString());
        }catch (NumberFormatException e){
            return 0l;
        }
        return aDouble;
    }

    public int getInt(Object key)
    {
        V value = get(key);
        if(value == null)return 0;
        int aDouble;
        try {
            aDouble = Integer.parseInt(value.toString());
        }catch (NumberFormatException e){
            return 0;
        }
        return aDouble;
    }



}
