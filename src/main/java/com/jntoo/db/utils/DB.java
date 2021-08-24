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

    /**
     * 自动带前缀得表信息
     * @param name 无前缀表
     * @return 返回QueryMap 实例
     */
    static public QueryMap name(String name)
    {
        return new QueryMap(name);
    }

    /**
     * 无前缀设置
     * @param table 完整得表名称
     * @return 返回QueryMap 实例
     */
    public static QueryMap table(String table){
        QueryMap queryMap = new QueryMap(table);
        queryMap.setPrefix("");
        return queryMap;
    }

    /**
     * 根据Class 创建表实例
     * @param cls 实体类得对象Class
     * @return 操作实体对象得值
     */
    public static<T> QueryWrapper<T> name(Class<T> cls)
    {
        return new QueryWrapper(cls);
    }

    /**
     * 根据实体对象操作
     * @param cls 实体对象得实例
     * @param <T> 实体对象
     * @return 操作实体对象得值
     */
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

    /**
     * 输出调试信息
     * @param message 调试信息
     */
    static public void log(String message)
    {
        if(Configuration.isDebug())
        {
            // 输出日期
            System.out.print(date("yyyy-MM-dd HH:mm:ss  "));
            System.out.println(message);
        }
    }

    /**
     * 输出调试信息2
     * @param message1 消息1
     * @param message2 消息2
     */
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

    /**
     * 获取当前日期
     * @param format 日期得格式
     * @return 日期信息
     */
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

    /**
     * 调试输出sql错误信息
     * @param e  错误信息
     * @param sql  附带得sql 语句
     */
    static public void log(SQLException e , String sql)
    {
        int code = e.getErrorCode();
        String message = e.getMessage();
        String errorMessage = String.format("SQL execute Error Code: %d sql: \n%s\nMessage:%s" ,code, sql,message);
        System.err.println(errorMessage);
    }
}
