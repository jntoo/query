package com.jntoo.db;

import com.jntoo.db.has.HasQuery;
import com.jntoo.db.model.LimitModel;
import com.jntoo.db.model.Options;
import com.jntoo.db.model.WhereModel;
import com.jntoo.db.utils.Convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class QueryOptions<TS extends QueryOptions> {
    protected Options options;

    protected void reset()
    {
        options = new Options();
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    /**
     * 设置字段为 获取所有字段
     *
     * @return 当前实例
     */
    public TS field() {
        return field("*");
    }

    /**
     * 设置字段，可以用","逗号隔开多个
     *
     * @param field 字段名
     * @return 当前实例
     */
    public TS field(String field) {
        //options.addField(field);
        options.addField(field);
        return (TS)this;
    }

    /**
     * 设置表
     *
     * @param nTable 设置表
     * @return 当前实例
     */
    public TS table(String nTable) {
        options.addTable(nTable);
        return (TS)this;
    }

    /**
     * 设置表
     *
     * @param nTable 当前表
     * @param alias  别名
     * @return 当前实例
     */
    public TS table(String nTable, String alias) {
        table(nTable + " " + alias);
        return (TS)this;
    }

    /**
     * 设置行数
     *
     * @param nLimit 获取得行数
     * @return 当前实例
     */
    public TS limit(String nLimit) {
        if (nLimit.indexOf(",") != -1) {
            String[] list = nLimit.split(",");
            return limit(Convert.toLong(list[0]), Convert.toLong(list[1]));
        }
        return limit(Convert.toLong(nLimit));
    }


    public TS hasQuery( HasQuery has )
    {
        options.addHasQuery(has);
        return (TS)this;
    }


    /**
     * 设置起始行和行数
     *
     * @param offset 获取得位置
     * @param nLimit 获取行数
     * @return 当前实例
     */
    public TS limit(String offset, String nLimit) {
        return limit(Convert.toLong(offset), Convert.toLong(nLimit));
    }

    /**
     * 设置是否锁表
     *
     * @param lock 是否锁表默认为false
     * @return 当前实例
     */
    public TS lock(boolean lock) {
        return this.lock(lock ? " FOR UPDATE " : "");
    }

    /**
     * 设置锁表代码
     *
     * @param lock 所部得代码
     * @return 当前实例
     */
    public TS lock(String lock) {
        options.setLock(lock);
        return (TS)this;
    }

    /**
     * 设置行数，字符串形式
     *
     * @param nLimit 行数
     * @return 当前实例
     */
    public TS limit(long nLimit) {
        options.setLimit(nLimit);
        return (TS)this;
    }

    /**
     * 设置起始行和行数
     *
     * @param offset 偏移位置
     * @param nLimit 获取行数
     * @return 当前实例
     */
    public TS limit(long offset, long nLimit) {
        options.setLimit(new LimitModel(offset , nLimit));
        return (TS)this;
    }

    protected Map queryAttrs = new HashMap();

    /**
     * 设置属性
     *
     * @param name  属性名
     * @param value 属性值
     * @return 当前实例
     */
    public TS setAttribute(String name, Object value) {
        queryAttrs.put(name, value);
        return (TS)this;
    }

    /**
     * 获取属性
     *
     * @param name 属性名
     * @return 属性值
     */
    public Object getAttribute(String name) {
        return queryAttrs.get(name);
    }



    /**
     * 设置SQL 分组
     *
     * @param nGroup 分组信息
     * @return 当前实例
     */
    public TS group(String nGroup) {
        options.addGroup(nGroup);
        return (TS)this;
    }

    /**
     * 设置 SQL 排序字段
     *
     * @param nOrder 字段名和排序信息
     * @return 当前实例
     */
    public TS order(String nOrder) {
        options.addOrder(nOrder);
        return (TS)this;
    }

    /**
     * 设置 SQL 排序字段
     *
     * @param nOrder 字段名
     * @param sort   排序名
     * @return 当前实例
     */
    public TS order(String nOrder, String sort) {
        order(nOrder + " " + sort);
        return (TS)this;
    }

    /**
     * 设置 SQL 排序字段
     *
     * @param nOrder 字段名
     * @return 当前实例
     */
    public TS orderDesc(String nOrder) {
        order(nOrder + " desc");
        return (TS)this;
    }

    /**
     * 设置 SQL 升序字段
     *
     * @param nOrder 字段名
     * @return 当前实例
     */
    public TS orderAsc(String nOrder) {
        order(nOrder + " asc");
        return (TS)this;
    }


    /**
     * 设置SQL语句使用全连接 会生成如下：INNER JOIN table t on cond 的形式
     *
     * @param table 表名 as 别名
     * @param cond  条件
     * @return 当前实例
     */
    public TS joinInner(String table, String cond) {
        return join(table, cond, "INNER");
    }

    /**
     * 设置sql 语句使用右连接 会生成如下：RIGHT JOIN table t on cond 的形式
     *
     * @param table 表名 as 别名
     * @param cond  条件
     * @return 当前实例
     */
    public TS joinRight(String table, String cond) {
        return join(table, cond, "RIGHT");
    }

    /**
     * 设置sql 语句使用左连接 会生成如下：table t on cond 的形式
     *
     * @param table 表名 as 别名
     * @param cond  条件
     * @return 当前实例
     */
    public TS joinLeft(String table, String cond) {
        return join(table, cond, "LEFT");
    }

    /**
     * 设置sql 语句使用右连接 会生成如下：type JOIN table t on cond 的形式
     *
     * @param table 表名 as 别名
     * @param cond  条件
     * @param type  跨不同类型
     * @return 当前实例
     */
    public TS join(String table, String cond, String type) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" ").append(type).append(" JOIN ").append(table).append(" ON ").append(cond);

        options.addJoin(buffer.toString());
        return (TS)this;
    }

    /**
     * 设置当前表的别名
     *
     * @param name 别名
     * @return 当前实例
     */
    public TS alias(String name) {
        options.setAlias(name);
        return (TS)this;
    }


    /**
     * 设置SQL条件
     *
     * @param name sql条件
     * @return 当前实例
     */
    public TS where(String name) {
        options.addWhere(new WhereModel(name));
        return (TS)this;
    }

    /**
     * 设置SQL条件 会自动写成 and name='value' 这样的形式
     *
     * @param name  字段名
     * @param value 条件值
     * @return 当前实例
     */
    public TS where(String name, Object value) {
        return where(name, null, value, null);
    }

    /**
     * 设置SQL条件 会自动写成 and name eq 'value' 这样的形式
     *
     * @param name  字段名
     * @param eq    符号，可以写成：“=、&gt;、&gt;=、&lt;、&lt;=、eq、neq、gt、egt、lt、elt”
     * @param value 值
     * @return 当前实例
     */
    public TS where(String name, String eq, Object value) {
        return where(name, eq, value, null);
    }

    /**
     * 设置SQL条件 会自动写成 and name eq 'value' 这样的形式
     *
     * @param name    字段名
     * @param eq      符号，可以写成：“=、&gt;、&gt;=、&lt;、&lt;=、eq、neq、gt、egt、lt、elt”
     * @param Value   值
     * @param connect 连接符默认为：and
     * @return 当前实例
     */
    public TS where(String name, String eq, Object Value, String connect) {

        /*HashMap list = new HashMap();
        list.put("name", name);
        list.put("exp", eq == null ? "=" : eq);
        list.put("value", Value == null ? "" : Value);
        list.put("connect", connect == null ? "and" : connect);*/

        options.addWhere(new WhereModel(name,eq,Value,connect));

        return (TS)this;
    }


    /**
     * 设置SQL条件 会自动写成 and field like inArray 这样的形式
     *
     * @param field   字段名
     * @param inArray 列表对象
     * @return 当前实例
     */
    public TS whereLike(String field, Object inArray) {
        return where(field, "like", inArray);
    }

    /**
     * 设置SQL条件 会自动写成 and field not like inArray 这样的形式
     *
     * @param field   字段名
     * @param inArray 字段值
     * @return 当前实例
     */
    public TS whereLikeNot(String field, Object inArray) {
        return where(field, "not like", inArray);
    }

    /**
     * 设置SQL条件 会自动写成 and field in(inArray1,inArray2) 这样的形式
     *
     * @param field   字段名
     * @param inArray 字段值
     * @return 当前实例
     */
    public TS whereIn(String field, Object inArray) {
        return where(field, "in", inArray);
    }

    /**
     * 设置SQL条件 会自动写成 and field not in(inArray1) 这样的形式
     *
     * @param field   字段名
     * @param inArray 字段值
     * @return 当前实例
     */
    public TS whereInNot(String field, Object inArray) {
        return where(field, "not in", inArray);
    }

    /**
     * 设置SQL条件 会自动写成 and field between inArray 这样的形式
     *
     * @param field   字段名
     * @param inArray 字段值
     * @return 当前实例
     */
    public TS whereBetween(String field, String inArray) {
        String[] arr = inArray.split(",");
        return where(field, "between", arr);
    }

    /**
     * 设置SQL条件 会自动写成 and field between 'start' and 'end' 这样的形式
     *
     * @param field 字段值
     * @param start 起始值
     * @param end   结束值
     * @return 当前实例
     */
    public TS whereBetween(String field, String start, String end) {
        List data = new ArrayList(2);
        data.add(0, start);
        data.add(1, end);
        return where(field, "between", data);
    }

    /**
     * 设置SQL条件 会自动写成 and field not between inArray 这样的形式
     *
     * @param field   字段名
     * @param inArray 使用,号隔开得起始和结束值
     * @return 当前实例
     */
    public TS whereBetweenNot(String field, String inArray) {
        String[] arr = inArray.split(",");
        return where(field, "not between", arr);
    }

    /**
     * 设置SQL条件 会自动写成 and field not between 'start' and 'end' 这样的形式
     *
     * @param field 字段名
     * @param start 起始值
     * @param end   结束值
     * @return 当前实例
     */
    public TS whereBetweenNot(String field, String start, String end) {
        List<String> data = new ArrayList(2);
        data.add(start);
        data.add(end);
        return where(field, "not between", data);
    }

}
