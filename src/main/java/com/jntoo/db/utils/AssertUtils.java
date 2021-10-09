package com.jntoo.db.utils;

public class AssertUtils {
    static public void isNull(Object object , String message , Object ...args)
    {
        assert object == null : String.format(message , args);
    }

    static public void condtion(boolean cond , String message , Object ...args)
    {
        assert cond : String.format(message , args);
    }

}
