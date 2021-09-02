package com.jntoo.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DefaultConnection implements ConnectionConfig {
    // 数据库名称
    static private String database = "";
    // 数据库账号
    static private String username = "";
    // 数据库密码
    static private String pwd = "";
    // 是否为 mysql8.0及以上、如果是则把 false 改成 true
    static private boolean isMysql8 = true;  // 是否为mysql8

    public static Connection conn = null;


    public Connection getConn() {
        try {
            if (conn == null || conn.isClosed()) {

                String connstr = getConnectString();
                conn = DriverManager.getConnection(connstr, username, pwd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    @Override
    public void closeConn(Connection connection) {

    }

    public static String getConnectString() {
        try {
            String connstr;
            if (!isMysql8) {
                Class.forName("com.mysql.jdbc.Driver");
                connstr = String.format("jdbc:mysql://localhost:3306/%s?useUnicode=true&characterEncoding=UTF-8&useOldAliasMetadataBehavior=true", database);
            } else {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connstr = String.format("jdbc:mysql://localhost:3306/%s?useUnicode=true&characterEncoding=UTF-8&useSSL=FALSE&serverTimezone=UTC&useOldAliasMetadataBehavior=true", database);
            }
            return connstr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDatabase() {
        return database;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPwd() {
        return pwd;
    }

    public static boolean isIsMysql8() {
        return isMysql8;
    }


    public static void setDatabase(String database) {
        DefaultConnection.database = database;
    }

    public static void setUsername(String username) {
        DefaultConnection.username = username;
    }

    public static void setPwd(String pwd) {
        DefaultConnection.pwd = pwd;
    }

    public static void setIsMysql8(boolean isMysql8) {
        DefaultConnection.isMysql8 = isMysql8;
    }
}
