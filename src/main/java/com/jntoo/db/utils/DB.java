package com.jntoo.db.utils;

import com.jntoo.db.Configuration;
import com.jntoo.db.ConnectionConfig;
import com.jntoo.db.QueryMap;
import com.jntoo.db.QueryWrapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class DB  {

    static public QueryMap name(String name)
    {
        return new QueryMap(name);
    }

    public static<T> QueryWrapper<T> name(Class<T> cls)
    {
        return new QueryWrapper(cls);
    }

    public static<T> QueryWrapper<T> name(T cls)
    {
        return new QueryWrapper(cls);
    }


    static public void release(Statement st , ResultSet rs)
    {
        try {
            if(rs != null) {
                rs.close();
            }
            if(st != null){
                st.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static public void log(String message)
    {
        if(Configuration.isDebug())
        {
            // 输出日期
            System.out.print(date("yyyy-MM-dd HH:mm:ss  "));
            System.out.println(message);
        }
    }

    static public void log(String message1 , Object message2)
    {
        if(Configuration.isDebug())
        {
            // 输出日期
            System.out.print(date("yyyy-MM-dd HH:mm:ss  "));
            System.out.println("------ "+message1);
            if(message2 instanceof Collection)
            {
                System.out.println("------ "+StringUtil.join(" , ",message2));
            }else{
                System.out.println("------ "+message2);
            }
        }
    }

    static public String date(String format)
    {
        try {//yyyyMMddHHmmss
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            Date currentTime_1 = new Date();
            return formatter.format(currentTime_1);
        } catch (Exception e) {
        }
        return "";
    }


    static public void log(SQLException e , String sql)
    {
        int code = e.getErrorCode();
        String message = e.getMessage();
        String errorMessage = String.format("SQL execute Error Code: %d sql: \n%s\nMessage:%s" ,code, sql,message);
        System.err.println(errorMessage);
    }
}
