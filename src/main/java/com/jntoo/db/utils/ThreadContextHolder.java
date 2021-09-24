package com.jntoo.db.utils;

import java.util.Map;

public class ThreadContextHolder {
    private final static ThreadLocal<Object> contexts = new ThreadLocal();

    public static<T> void setContext( T data )
    {
        contexts.set(data);
    }

    public static<T> T getContext(Class<T> tClass)
    {
        return (T)contexts.get();
    }

    public static<T> T getContext()
    {
        return (T)contexts.get();
    }

    public static void remove()
    {
        contexts.remove();
    }
}
