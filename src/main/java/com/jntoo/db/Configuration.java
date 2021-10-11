package com.jntoo.db;

import com.jntoo.db.build.Builder;
import com.jntoo.db.build.Mysql;
import com.jntoo.db.build.SqlServer;
import com.jntoo.db.utils.AssertUtils;
import javafx.application.Application;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


public class Configuration {
    private static QueryConfig queryConfig = null;

    static {
        if(queryConfig == null){
            ConfigLoadProperties.init();
        }
    }

    @Deprecated
    static public ConnectionConfig getConnectionConfig()
    {
        return queryConfig.getConnectionConfig();
    }

    static public Connection getConnection()
    {
        DataSource dataSource = queryConfig.getDataSource();
        if( dataSource != null ){
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(queryConfig.getConnectionConfig() != null){
            return queryConfig.getConnectionConfig().getConn();
        }
        throw new RuntimeException("not set Configuration");
    }

    public static void closeConnection(Connection connection)
    {
        //AssertUtils.isNull(connection , "close connection class empty null");
        DataSource dataSource = queryConfig.getDataSource();
        if(dataSource!= null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }

        if(queryConfig.getConnectionConfig() != null) {
            queryConfig.getConnectionConfig().closeConn(connection);
        }
    }

    static public String getPrefix()
    {
        return queryConfig.getPrefix();
    }

    /**
     * 获取 是否为调试模式
     *
     * @return 是否为调试模式
     */
    public static boolean isDebug() {
        return queryConfig.isDebug();
    }
    public static QueryConfig getQueryConfig() {
        return queryConfig;
    }

    public static Builder getBuilder()
    {
        Class aClass = queryConfig.getBuilder();

        try {
            return (Builder)aClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("没找到数据库Builder 编译类");
    }

    public static synchronized void setQueryConfig(QueryConfig queryConfig) {
        if(Configuration.queryConfig != null)
        {
            System.out.println("[WARE] Set up many times QueryConfig class");
        }

        if(queryConfig.getBuilder() == null && queryConfig.getDataSource() != null ){
            Connection conn = null;
            try {
                conn = queryConfig.getDataSource().getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            setBuilderType(queryConfig , conn);
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(queryConfig.getBuilder() == null && queryConfig.getConnectionConfig() != null ){
            Connection conn = queryConfig.getConnectionConfig().getConn();
            setBuilderType(queryConfig , conn);
            queryConfig.getConnectionConfig().closeConn(conn);
        }
        AssertUtils.isNull(queryConfig.getBuilder() , "not set Builder class");
        Configuration.queryConfig = queryConfig;
    }

    private static void setBuilderType(QueryConfig queryConfig , Connection conn )
    {
        if(conn == null){
            return;
        }

        String str = conn.toString();
        Class aClass = null;
        if( str.indexOf("com.mysql") != -1 ) {
            aClass = Mysql.class;
        } else {
            aClass = SqlServer.class;
        }
        queryConfig.setBuilder(aClass);
        System.out.println(String.format("[Info] auto binding builder %s " , aClass));
    }


}
