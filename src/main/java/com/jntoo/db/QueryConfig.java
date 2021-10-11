package com.jntoo.db;

import com.jntoo.db.build.Builder;

import javax.sql.DataSource;

public class QueryConfig {
    /**
     * 调试输出
     */
    private boolean isDebug = false;
    /**
     * 获取链接信息
     */
    private ConnectionConfig connectionConfig = null;
    private DataSource dataSource = null;
    /**
     * 表前缀
     */
    private String prefix = "";
    private Class<? extends Builder> builder = null;
    public boolean isDebug() {
        return isDebug;
    }
    public void setDebug(boolean debug) {
        isDebug = debug;
    }
    public void beanInit()
    {
        if(Configuration.getQueryConfig() != this){
            Configuration.setQueryConfig(this);
        }
    }
    public ConnectionConfig getConnectionConfig() {
        return connectionConfig;
    }
    public void setConnectionConfig(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
    }
    public String getPrefix() {
        return prefix;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    public Class<? extends Builder> getBuilder() {
        return builder;
    }
    public void setBuilder(Class<? extends Builder> builder) {
        this.builder = builder;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
