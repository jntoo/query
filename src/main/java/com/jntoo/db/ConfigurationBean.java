package com.jntoo.db;

import com.jntoo.db.build.Builder;

import javax.sql.DataSource;

public class ConfigurationBean {

    /**
     * 数据源
     */
    private DataSource dataSource;
    /**
     * 表前缀
     */
    private String prefix;

    /**
     * 是否输出调试信息
     */
    private boolean debug;

    /**
     * 设置编译信息
     */
    private Class<? extends Builder> builder;

    public ConfigurationBean() {

    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Class<? extends Builder> getBuilder() {
        return builder;
    }

    public void setBuilder(Class<? extends Builder> builder) {
        this.builder = builder;
    }
}
