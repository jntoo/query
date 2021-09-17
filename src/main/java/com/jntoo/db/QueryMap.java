package com.jntoo.db;


import java.util.Map;


public class QueryMap extends QueryWrapperBase<Map , QueryMap> {
    /**
     * 构造Query
     *
     * @param name 表名
     */
    public QueryMap(String name) {
        super(name);
    }
}
