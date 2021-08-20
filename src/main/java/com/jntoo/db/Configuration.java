package com.jntoo.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlException;
import java.util.Properties;

public class Configuration {
    private static Properties defaultProperty;
    private static QueryConfig queryConfig;
    static {
        init();
    }

    static void init() {
        //初始化默认配置
        defaultProperty = new Properties();
        queryConfig = new QueryConfig();
        defaultProperty.setProperty("jntoo.db.debug", "true");
        defaultProperty.setProperty("jntoo.db.connection", "com.jntoo.db.DefaultConnection");
        defaultProperty.setProperty("jntoo.db.prefix", "");

        //读取自定义配置
        String t4jProps = "jntoodb.properties";
        boolean loaded = loadProperties(defaultProperty, "." + File.separatorChar + t4jProps)
                || loadProperties(defaultProperty, Configuration.class.getResourceAsStream("/WEB-INF/" + t4jProps))
                || loadProperties(defaultProperty, Configuration.class.getClassLoader().getResourceAsStream(t4jProps));
        if (!loaded) {
            System.out.println("weixin4j:没有加载到weixin4j.properties属性文件!");
        }
        setConfig();
    }


    static private void setConfig()
    {
        queryConfig.setDebug(getBoolean("jntoo.db.debug"));
        Object config = getPropertyClass("jntoo.db.connection");

        if(config instanceof ConnectionConfig){
            queryConfig.setConnectionConfig((ConnectionConfig) config);
        }
        queryConfig.setPrefix(getProperty("jntoo.db.prefix"));
    }


    static public ConnectionConfig getConnectionConfig()
    {
        return queryConfig.getConnectionConfig();
    }
    static public String getPrefix()
    {
        return queryConfig.getPrefix();
    }

    /**
     * 加载属性文件
     *
     * @param props 属性文件实例
     * @param path 属性文件路径
     * @return 是否加载成功
     */
    private static boolean loadProperties(Properties props, String path) {
        try {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                props.load(new FileInputStream(file));
                return true;
            }
        } catch (IOException ignore) {
            //异常忽略
            ignore.printStackTrace();
        }
        return false;
    }

    /**
     * 加载属性文件
     *
     * @param props 属性文件实例
     * @param is 属性文件流
     * @return 是否加载成功
     */
    private static boolean loadProperties(Properties props, InputStream is) {
        try {
            if (is != null) {
                props.load(is);
                return true;
            }
        } catch (IOException ignore) {
            //异常忽略
            ignore.printStackTrace();
        }
        return false;
    }


    /**
     * 获取 是否为调试模式
     *
     * @return 是否为调试模式
     */
    public static boolean isDebug() {
        return queryConfig.isDebug();
    }




    public static boolean getBoolean(String name) {
        String value = getProperty(name);
        return Boolean.valueOf(value);
    }

    public static int getIntProperty(String name) {
        String value = getProperty(name);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    public static int getIntProperty(String name, int fallbackValue) {
        String value = getProperty(name, String.valueOf(fallbackValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    /**
     * 获取属性值
     *
     * @param name 属性名称
     * @return 属性值
     */
    public static String getProperty(String name) {
        return getProperty(name, null);
    }

    public static Object getPropertyClass(String name)
    {
        try {
            Class connect = Class.forName(getProperty(name));
            try {
                return connect.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取属性值
     *
     * @param name 属性名
     * @param fallbackValue 默认返回值
     * @return 属性值
     */
    public static String getProperty(String name, String fallbackValue) {
        String value;
        try {
            //从全局系统获取
            value = System.getProperty(name, null);
            if (null == value) {
                //先从系统配置文件获取
                value = defaultProperty.getProperty(name, fallbackValue);
            }
        } catch (AccessControlException ace) {
            // Unsigned applet cannot access System properties
            value = fallbackValue;
        }
        return value;
    }

    public static QueryConfig getQueryConfig() {
        return queryConfig;
    }

    public static void setQueryConfig(QueryConfig queryConfig) {
        Configuration.queryConfig = queryConfig;
    }


}
