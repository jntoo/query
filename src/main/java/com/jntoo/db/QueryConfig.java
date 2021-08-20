package com.jntoo.db;

public class QueryConfig {
    /**
     * 调试输出
     */
    private boolean isDebug;

    /**
     * 获取链接信息
     */
    private ConnectionConfig connectionConfig;

    /**
     * 表前缀
     */
    private String prefix;

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
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
}
