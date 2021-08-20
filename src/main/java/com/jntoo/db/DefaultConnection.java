package com.jntoo.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DefaultConnection implements ConnectionConfig {
    // 数据库名称
    static private final String database = "javamvc08652gxstglxt";
    // 数据库账号
    static private final String username = "root";
    // 数据库密码
    static private final String pwd = "root";
    // 是否为 mysql8.0及以上、如果是则把 false 改成 true
    static private final boolean isMysql8 = false;  // 是否为mysql8

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
}
