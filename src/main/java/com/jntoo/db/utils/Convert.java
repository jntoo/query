package com.jntoo.db.utils;

public class Convert {
    public static int toInt( Object value )
    {
        try{
            return Integer.parseInt(String.valueOf(value));
        }catch (Exception e){
            return 0;
        }
    }

    public static Integer toInteger( Object value )
    {
        try{
            return Integer.valueOf(String.valueOf(value));
        }catch (Exception e){
            return 0;
        }
    }

    public static long toLong( Object value )
    {
        try{
            return Long.parseLong(String.valueOf(value));
        }catch (Exception e){
            return 0;
        }
    }

    public static Long toLongc( Object value )
    {
        try{
            return Long.valueOf(String.valueOf(value));
        }catch (Exception e){
            return 0L;
        }
    }

    public static double toDouble( Object value )
    {
        try{
            return Double.parseDouble(String.valueOf(value));
        }catch (Exception e){
            return 0;
        }
    }

    public static Double toDoublec( Object value )
    {
        try{
            return Double.valueOf(String.valueOf(value));
        }catch (Exception e){
            return 0.0;
        }
    }

    public static float toFloat( Object value )
    {
        try{
            return Float.parseFloat(String.valueOf(value));
        }catch (Exception e){
            return 0;
        }
    }

    public static Float toFloatc( Object value )
    {
        try{
            return Float.valueOf(String.valueOf(value));
        }catch (Exception e){
            return 0f;
        }
    }
}
