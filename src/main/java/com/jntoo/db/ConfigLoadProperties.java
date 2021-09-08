package com.jntoo.db;

import com.jntoo.db.build.Builder;
import com.jntoo.db.utils.StringUtil;

import javax.management.Query;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlException;
import java.util.Properties;

public class ConfigLoadProperties {

    static protected void init()
    {
        String t4jProps = "jntoodb.properties";
        init(t4jProps);
    }

    static public void init(String filename) {
        //初始化默认配置
        Properties defaultProperty = new Properties();
        defaultProperty.setProperty("jntoo.db.debug", "true");
        defaultProperty.setProperty("jntoo.db.connection", "");
        //defaultProperty.setProperty("jntoo.db.connection", "com.jntoo.db.DefaultConnection");
        defaultProperty.setProperty("jntoo.db.prefix", "");
        defaultProperty.setProperty("jntoo.db.builder-class", "");

        //读取自定义配置
        String t4jProps = filename;
        boolean loaded = loadProperties(defaultProperty, "." + File.separatorChar + t4jProps)
                || loadProperties(defaultProperty, ConfigLoadProperties.class.getResourceAsStream("/WEB-INF/" + t4jProps))
                || loadProperties(defaultProperty, ConfigLoadProperties.class.getClassLoader().getResourceAsStream(t4jProps));
        if (!loaded) {
            //System.out.println("没有加载到"+t4jProps+"属性文件!");
        }
        setConfig(defaultProperty);
    }
    

    static public void setConfig(Properties defaultProperty)
    {
        QueryConfig queryConfig = new QueryConfig();
        queryConfig.setDebug(getBoolean(defaultProperty,"jntoo.db.debug"));
        Object config = getPropertyClass(defaultProperty,"jntoo.db.connection");

        if(config instanceof ConnectionConfig){
            queryConfig.setConnectionConfig((ConnectionConfig) config);
        }
        queryConfig.setPrefix(getProperty(defaultProperty,"jntoo.db.prefix"));
        String builderString = getProperty(defaultProperty,"jntoo.db.builder-class");
        if(!StringUtil.isNullOrEmpty(builderString)){
            try {
                Class aClass = Class.forName(builderString);
                if(Builder.class.isAssignableFrom(aClass))
                {
                    queryConfig.setBuilder(aClass);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        Configuration.setQueryConfig(queryConfig);
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

    public static boolean getBoolean(Properties defaultProperty, String name) {
        String value = getProperty(defaultProperty,name);
        return Boolean.valueOf(value);
    }

    public static int getIntProperty(Properties defaultProperty,String name) {
        String value = getProperty(defaultProperty,name);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    public static int getIntProperty(Properties defaultProperty , String name, int fallbackValue) {
        String value = getProperty(defaultProperty,name, String.valueOf(fallbackValue));
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
    public static String getProperty(Properties defaultProperty , String name) {
        return getProperty(defaultProperty,name, null);
    }


    public static Object getPropertyClass(Properties defaultProperty , String name)
    {
        try {
            String className = getProperty(defaultProperty,name);
            if(!StringUtil.isNullOrEmpty(className)){
                Class connect = Class.forName(className);
                try {
                    return connect.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
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
    public static String getProperty(Properties defaultProperty,String name, String fallbackValue) {
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

}
