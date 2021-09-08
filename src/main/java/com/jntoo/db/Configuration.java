package com.jntoo.db;

import com.jntoo.db.build.Builder;
import com.jntoo.db.build.Mysql;
import com.jntoo.db.build.SqlServer;
import java.sql.Connection;


/**
 * @author weixin4j
 */
public class Configuration {
    private static QueryConfig queryConfig = null;

    static {
        if(queryConfig == null){
            ConfigLoadProperties.init();
        }
    }

    static public ConnectionConfig getConnectionConfig()
    {
        return queryConfig.getConnectionConfig();
    }

    static public ConnectionConfig getConnection()
    {
        return queryConfig.getConnectionConfig();
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
        if(aClass == null){
            throw new RuntimeException("没找到数据库Builder 编译类");
        }
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
        if(queryConfig.getBuilder() == null && queryConfig.getConnectionConfig() != null ){
            Connection conn = queryConfig.getConnectionConfig().getConn();
            if(conn != null){
                String str = conn.toString();
                Class aClass = null;
                if(str.indexOf("com.mysql") != -1) {
                    aClass = Mysql.class;
                } else {
                    aClass = SqlServer.class;
                }
                queryConfig.setBuilder(aClass);
            }
            queryConfig.getConnectionConfig().closeConn(conn);
        }
        Configuration.queryConfig = queryConfig;
    }


}
