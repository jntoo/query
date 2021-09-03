package com.jntoo.db;


import com.alibaba.fastjson.*;
import com.jntoo.db.annotation.FieldType;
import com.jntoo.db.annotation.Fields;
import com.jntoo.db.annotation.Table;
import com.jntoo.db.build.Builder;

import com.jntoo.db.model.FieldInfoModel;
import com.jntoo.db.model.QMap;
import com.jntoo.db.model.TableModel;
import com.jntoo.db.utils.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * 操作数据库链式执行
 * 目前只实现了部分方法，之后会继续完善该代码,让其支持实体类的数据获取
 * 使用方法：Query.make("表名称").where("字段名" , "条件符号","条件值").select()
 */
public class QueryWrapper<T> {
    protected String mName = "";
    protected HashMap mOption = null;
    protected String pk = "id";
    protected String prefix = ""; // 设置表前缀
    protected HashMap mData = null;
    protected Builder builder = null;
    private TableModel tableModel;
    protected T model;


    public QueryWrapper() {
        reset();
        model = getInstanceOfT();
        handlerClass();
    }

    /**
     * 构造Query
     *
     * @param name 表名
     */
    public QueryWrapper(String name) {
        reset();
        model = (T)new HashMap();
        setName(name);
    }

    /**
     * 构造Query
     *
     * @param cls 实体类得Class
     */
    public QueryWrapper(Class<T> cls) {
        reset();
        try {
            model = cls.newInstance();
            handlerClass();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构造Query
     *
     * @param cls 实体类得实例
     */
    public QueryWrapper(T cls) {
        reset();
        model = cls;
        handlerClass();
        Map map = entityToMap(cls);
        mData.putAll(map);
    }

    private void handlerClass() {
        T instance = getInstanceOfT();
        if (!(instance instanceof Map)) {
            tableModel = TableManageUtils.getTable(instance.getClass());
            if (!StringUtil.isNullOrEmpty(tableModel.pk)) {
                setPk(tableModel.pk);
            }
            setPrefix(tableModel.prefix);
            setName(tableModel.name);
        }
    }

    /**
     * 重置并初始化数据
     *
     * @return 重置数据信息
     */
    protected QueryWrapper<T> reset() {
        ConnectionConfig connectionConfig = Configuration.getConnectionConfig();
        if (connectionConfig == null) {
            throw new RuntimeException("not ConnectionConfig");
        }
        model = null;
        mName = "";
        mOption = null;
        mOption = new HashMap();
        mData = new HashMap();

        builder = Builder.make();
        prefix = Configuration.getPrefix();
        return this;
    }


    /**
     * 设置一个字段自增
     *
     * @param field 自增得字段
     * @param step  自增步长
     * @return QueryWrapper实例
     */
    public QueryWrapper<T> inc(String field, int step) {
        if (step < 1) step = 1;
        ArrayList list = new ArrayList();
        list.add("inc");
        list.add(step);
        data(field, list);
        return this;
    }

    /**
     * 设置一个字段自减
     *
     * @param field 自增得字段
     * @param step  自增得步长
     * @return QueryWrapper实例
     */
    public QueryWrapper<T> dec(String field, int step) {
        if (step < 1) step = 1;
        ArrayList list = new ArrayList();
        list.add("dec");
        list.add(step);

        data(field, list);
        return this;
    }

    /**
     * 马上更新数据字段自增1
     *
     * @param field 自增得字段
     * @return 是否成功
     */
    public boolean setInc(String field) {
        return setInc(field, 1);
    }

    /**
     * 马上更新数据字段自增step
     *
     * @param field 设置自增得字段
     * @param step  自增步长
     * @return 是否设置成功
     */
    public boolean setInc(String field, String step) {
        return inc(field, Integer.valueOf(step).intValue()).update();
    }

    /**
     * 马上更新数据字段自增step
     *
     * @param field 设置自增得字段
     * @param step  自增步长
     * @return 是否设置成功
     */
    public boolean setInc(String field, int step) {
        return inc(field, step).update();
    }

    /**
     * 马上更新数据字段自减1
     *
     * @param field 设置自减1得字段
     * @return 是否成功
     */
    public boolean setDec(String field) {
        return setDec(field, 1);
    }

    /**
     * 马上更新数据字段自减step
     *
     * @param field 设置自减得字段
     * @param step  设置自减得步长
     * @return 是否成功
     */
    public boolean setDec(String field, String step) {
        return dec(field, Integer.valueOf(step).intValue()).update();
    }

    /**
     * 马上更新数据字段自减step
     *
     * @param field 设置自减得字段
     * @param step  设置自减得步长
     * @return 是否成功
     */
    public boolean setDec(String field, int step) {
        return dec(field, step).update();
    }

    /**
     * 设置某字段为某个值，并更新
     *
     * @param field 字段名称
     * @param step  某字段信息
     * @return 是否成功
     */
    public boolean setField(String field, Object step) {
        data(field, step);
        return update();
    }

    /**
     * 获取当前写入的data
     *
     * @return 获取当前写入得信息
     */
    public Map getData() {
        return mData;
    }

    /**
     * 更新当前数据
     *
     * @return 立即更新表
     */
    public boolean update() {
        return update(null);
    }


    /**
     * 更新当前数据加写入的data
     *
     * @param updateData 设置更新得对象
     * @return 是否成功
     */
    public boolean update(T updateData) {
        if (updateData != null) {
            model = updateData;
            mData.putAll(entityToMap(updateData));
        }
        if (getOptionArrayList("where").size() == 0) {
            if (!mData.containsKey(getPk())) // 没有条件，不更新
            {
                return false;
            }
            where(getPk(), mData.get(getPk()));
        }

        if (model != null && !(model instanceof Map)) {
            if (tableModel.autoUpdateTimeField.size() > 0) {
                handlerAutoTimerField(tableModel.autoUpdateTimeField);
            }
            if (tableModel.autoUpdateField.size() > 0) {
                handlerAutoField(tableModel.autoUpdateField);
            }
        }

        String sql = builder.buildUpdate(this);
        executeUpdate(sql);
        return true;
    }

    private void handlerAutoTimerField(List<String> fields) {
        // 自动更新时间
        for (String s : fields) {
            FieldInfoModel info = tableModel.getFieldInfo(s);
            Class<?> type = info.getField().getType();
            if (type == Long.class) {
                // 时间戳
                Long time = TimerUtils.time();
                mData.put(info.getName(), time);
                try {
                    info.getSetMethod().invoke(model, time);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else if (Date.class.isAssignableFrom(type)) {
                mData.put(info.getName(), TimerUtils.getDateStr()); // 直接写string 格式进去就好
                try {
                    info.getSetMethod().invoke(model, type.newInstance());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            } else if (String.class == type) {
                mData.put(info.getName(), TimerUtils.getDateStr());
                try {
                    info.getSetMethod().invoke(model, TimerUtils.getDateStr());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void handlerAutoField(List<String> autoFields) {
        if (autoFields.size() > 0) {
            Map clone = (Map) mData.clone();
            for (String s : autoFields) {
                FieldInfoModel fieldInfo = tableModel.getFieldInfo(s);
                if (fieldInfo.getAutoMethodUpdate() != null) {
                    try {
                        Object result = fieldInfo.getAutoMethodUpdate().invoke(model, clone);
                        mData.put(fieldInfo.getName(), result);
                        if (result.getClass().isAssignableFrom(fieldInfo.getField().getType())) {
                            // 格式必须一致，否则失败
                            TableManageUtils.setFieldValue(fieldInfo.getField(), model, result);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    // 剩下得值就不写了
                    mData.put(fieldInfo.getName(), fieldInfo.getAutoMethodUpdateString());
                }
            }
        }
    }


    private Map entityToMap(T data) {
        if (data instanceof Map) {
            return (Map) data;
        } else {
            HashMap map = new HashMap();
            try {
                Class c = data.getClass();
                Field[] fields = c.getDeclaredFields();
                for (Field field : fields) {
                    FieldInfoModel fieldInfo = tableModel.getFieldInfo(field.getName());
                    if (fieldInfo == null) continue;
                    String name = fieldInfo.getName(); //getFieldName(field);
                    Fields annotation = fieldInfo.getAnnField(); //field.getAnnotation(Fields.class);
                    boolean isAcc = field.isAccessible();
                    if (!isAcc) {
                        field.setAccessible(true);
                    }
                    Object value = field.get(data);
                    if (!isAcc) {
                        field.setAccessible(false);
                    }
                    if (value != null) {
                        if (annotation != null && annotation.type() == FieldType.JSON) {
                            value = JSON.toJSONString(value);
                        }
                        map.put(name, value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        }
    }


    /**
     * 向query 写入data
     *
     * @param value 设置值
     * @return 当前实例
     */
    public QueryWrapper<T> data(Map value) {
        Map<String, Object> datas = value;
        for (Map.Entry<String, Object> entry : datas.entrySet()) {
            data(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * 向当前query 写入data
     *
     * @param name  字段名
     * @param value 属性值
     * @return 当前实例
     */
    public QueryWrapper<T> data(String name, Object value) {
        String field = name;
        if (tableModel != null) {
            FieldInfoModel fieldInfo = tableModel.getFieldInfo(field);
            if (fieldInfo != null) {
                field = fieldInfo.getName();
            }
        }

        mData.put(field, value);
        return this;
    }

    /**
     * 向当前query 写入data
     *
     * @param name  字段名
     * @param value 属性值
     * @return 当前实例
     */
    public QueryWrapper<T> data(String name, boolean value) {
        mData.put(name, value ? 1 : 0);
        return this;
    }

    public int insert() {
        return insert(null, false);
    }

    /**
     * 插入数据
     *
     * @param insertData 插入得数据
     * @return 主键自增值得id
     */
    public int insert(T insertData) {
        return insert(insertData, false);
    }

    /**
     * 插入数据
     *
     * @param insertData 插入得数据
     * @param replace    是否使用替换
     * @return 主键自增值得id
     */
    public int insert(T insertData, boolean replace) {
        if (insertData != null) {
            model = insertData;
            mData.putAll(entityToMap(insertData));
        }

        if (model != null && !(model instanceof Map)) {
            if (tableModel.autoInsertTimeField.size() > 0) {
                handlerAutoTimerField(tableModel.autoInsertTimeField);
            }
            if (tableModel.autoInserField.size() > 0) {
                handlerAutoField(tableModel.autoInserField);
            }
        }
        String sql = builder.buildInsert(this, replace);
        int id = executeInsert(sql);
        if (model instanceof Map && model != null) {
            if (model != null) {
                ((Map) model).put(getPk(), id);
            }
        } else {
            try {
                String pk = getPk();
                String methodName = "set" + StringUtil.firstCharUpper(pk);// pk.substring(0,1).toUpperCase()+pk.substring(1);
                if (model != null && !(model instanceof Map)) {
                    Method setId = model.getClass().getMethod(methodName, Integer.class);
                    setId.invoke(model, id);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return id;
    }


    /**
     * 获取当前自增字段名称
     *
     * @return 当前表得主键
     */
    public String getPk() {
        return pk;
    }

    /**
     * 设置自增字段名
     *
     * @param pk 设置表得主键
     */
    public void setPk(String pk) {
        this.pk = pk;
    }

    /**
     * 尚未实现该代码，获取表的数据
     */
    protected void finalize() {

        //free();
    }

    /**
     * 释放资源
     */
    /*public void free() {
        // 释放rs

        for (int i = 0; i < resultSetList.size(); i++) {
            Object os = resultSetList.get(i);
            try {
                if (os instanceof Statement) {
                    Statement st = ((Statement) os);
                    st.close();
                } else if (os instanceof ResultSet) {
                    ((ResultSet) os).close();
                }else if(os instanceof Connection){

                }
            } catch (SQLException e) {
            }
        }
        resultSetList.clear();
    }*/

    /**
     * 设置表名称
     *
     * @param name 表名称，不带前缀
     * @return 当前实例
     */
    public QueryWrapper<T> setName(String name) {
        mName = name;
        return this;
    }

    /**
     * 获取表名称
     *
     * @return 当前表名称
     */
    public String getName() {
        return mName;
    }

    /**
     * 设置属性
     *
     * @param name  属性名
     * @param value 属性值
     * @return 当前实例
     */
    public QueryWrapper<T> setAttribute(String name, Object value) {
        getOptionHashMap("data").put(name, value);
        return this;
    }

    /**
     * 获取属性
     *
     * @param name 属性名
     * @return 属性值
     */
    public Object getAttribute(String name) {
        return getOptionHashMap("data").get(name);
    }

    /**
     * 设置字段为 获取所有字段
     *
     * @return 当前实例
     */
    public QueryWrapper<T> field() {
        return field("*");
    }

    /**
     * 设置字段，可以用","逗号隔开多个
     *
     * @param field 字段名
     * @return 当前实例
     */
    public QueryWrapper<T> field(String field) {
        getOptionArrayList("field").add(field);
        return this;
    }

    /**
     * 设置表
     *
     * @param nTable 设置表
     * @return 当前实例
     */
    public QueryWrapper<T> table(String nTable) {
        getOptionArrayList("table").add(nTable);
        return this;
    }

    /**
     * 设置表
     *
     * @param nTable 当前表
     * @param alias  别名
     * @return 当前实例
     */
    public QueryWrapper<T> table(String nTable, String alias) {
        getOptionArrayList("table").add(nTable + " " + alias);
        return this;
    }

    /**
     * 设置行数
     *
     * @param nLimit 获取得行数
     * @return 当前实例
     */
    public QueryWrapper<T> limit(String nLimit) {
        if (nLimit.indexOf(",") != -1) {
            String[] list = nLimit.split(",");
            return limit(Convert.toLong(list[0]), Convert.toLong(list[1]));
        }

        return limit(Convert.toLong(nLimit));
    }

    /**
     * 设置起始行和行数
     *
     * @param offset 获取得位置
     * @param nLimit 获取行数
     * @return 当前实例
     */
    public QueryWrapper<T> limit(String offset, String nLimit) {
        return limit(Convert.toLong(offset), Convert.toLong(nLimit));
    }

    /**
     * 设置是否锁表
     *
     * @param lock 是否锁表默认为false
     * @return 当前实例
     */
    public QueryWrapper<T> lock(boolean lock) {
        return this.lock(lock ? " FOR UPDATE " : "");
    }

    /**
     * 设置锁表代码
     *
     * @param lock 所部得代码
     * @return 当前实例
     */
    public QueryWrapper<T> lock(String lock) {
        getOption().put("lock", lock);
        return this;
    }

    /**
     * 设置行数，字符串形式
     *
     * @param nLimit 行数
     * @return 当前实例
     */
    public QueryWrapper<T> limit(long nLimit) {
        getOptionHashMap("limit").put("limit", nLimit);
        return this;
    }

    /**
     * 设置起始行和行数
     *
     * @param offset 偏移位置
     * @param nLimit 获取行数
     * @return 当前实例
     */
    public QueryWrapper<T> limit(long offset, long nLimit) {
        HashMap map = getOptionHashMap("limit");
        map.put("limit", nLimit);
        map.put("offset", offset);
        return this;
    }

    /**
     * 根据ID 获取一行数据
     *
     * @param id 主键id
     * @return ${T}实例信息
     */
    public T find(Object id) {
        where(pk, id);
        return find();
    }

    /**
     * 根据当前条件获取一行数据
     *
     * @return ${T}实例信息
     */
    public T find() {
        //limit(1);
        String sql = builder.buildSelect(this);
        return (T)DB.find(sql , model != null ? model.getClass() : Map.class , builder.getBindData().toArray());

        /*ResultSet rs = query(sql);
        T data = fetchEntity(rs);
        free();
        return data;*/
    }

    /**
     * 根据当前条件获取一行数据
     *
     * @return 获取Map
     */
    private Map findMap() {
        //limit(1);
        String sql = builder.buildSelect(this);
        return DB.find(sql , builder.getBindData().toArray());

        /*ResultSet rs = query(sql);
        Map data = fetch(rs);
        free();
        return data;*/
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * 生成统计计算语句
     *
     * @param f    统计得字段
     * @param func 类型如：sum、max、min、avg
     * @return 统计后得值
     */
    protected double total(String f, String func) {
        String ifnull = builder.parseIfNull(func + "(" + f + ")", "0");
        String field = ifnull + " count";
        if (mOption.containsKey("field")) {
            getOptionArrayList("field").clear();
        }
        getOptionArrayList("field").add(field);
        Map data = findMap();
        if (data.containsKey("count")) {
            String count = data.get("count").toString();
            return Double.valueOf(count).doubleValue();
        }
        return 0;
    }

    /**
     * 求某字段和
     *
     * @param field 字段名
     * @return 求和后得结果
     */
    public double sum(String field) {
        return total(field, "SUM");
    }

    /**
     * 求某字段的平均值
     *
     * @param field 字段名
     * @return 求平均值得结果
     */
    public double avg(String field) {
        return total(field, "AVG");
    }

    /**
     * 求最大值
     *
     * @param field 字段名
     * @return 最大值
     */
    public double max(String field) {
        return total(field, "MAX");
    }

    /**
     * 求最小值
     *
     * @param field 字段名
     * @return 最小值
     */
    public double min(String field) {
        return total(field, "MIN");
    }

    /**
     * 求数据行数
     *
     * @return 总行数
     */
    public long count() {
        return count(null);
    }

    /**
     * 根据字段名求数据行数
     * @param field 字段名，可为null
     * @return 根据字段得某行数
     */
    public long count(String field) {
        if (field == null) {
            if (mOption.containsKey("alias")) {
                field = "count(" + mOption.get("alias") + ".id) count";
            } else {
                field = "count(*) count";
            }
        } else {
            field = "count(" + field + ") count";
        }
        if (mOption.containsKey("field")) {
            mOption.put("field", new ArrayList());
            //getOptionArrayList("field").clear();
        }
        if (mOption.containsKey("order")) {
            mOption.remove("order");
        }
        getOptionArrayList("field").add(field);
        Map data = findMap();
        if (data.containsKey("count")) {
            return Long.valueOf((String) data.get("count")).longValue();
        }
        return 0;
    }


    /**
     * 根据id 删除数据
     *
     * @param id 根据id 、或者List 列表删除数据行
     * @return 返回删除得行数
     */
    public long delete(Object id) {
        if (id instanceof Collection) {
            where(getPk(), "in", id);
        } else if (id instanceof String) {
            String idObject = (String) id;
            if (idObject.indexOf(",") != -1) {
                where(getPk(), "in", idObject);
            } else {
                where(getPk(), id);
            }
        } else {
            where(getPk(), id);
        }
        return delete();
    }


    /**
     * 根据当前条件删除数据，如果没有条件则不执行删除
     *
     * @return 删除得行数
     */
    public long delete() {
        if (!mOption.containsKey("where")) {
            return -1;
        }
        String sql = this.builder.buildDelete(this);
        return executeUpdate(sql);
    }

    public QueryWrapper<T> where(T data) {
        if (data == null) {
            return this;
        }
        if (data instanceof Map) {
            Map<String, Object> map = (Map) data;
            for (Map.Entry<String, Object> o : map.entrySet()) {
                where(o.getKey(), o.getValue());
            }
        } else {
            try {
                Field[] fields = data.getClass().getDeclaredFields();
                for (Field field : fields) {
                    FieldInfoModel fieldInfo = tableModel.getFieldInfo(field.getName());
                    if(fieldInfo == null) continue;

                    boolean isAcc = field.isAccessible();
                    if (!isAcc) field.setAccessible(true);
                    Object value = field.get(model);
                    if (!isAcc) field.setAccessible(false);
                    if (value != null) {
                        where(getFieldName(field), value);
                    }
                }
            } catch (Exception e) {

            }
        }
        return this;
    }

    /**
     * 根据当前条件获取数据集
     *
     * @return 列表行
     */
    public List select() {
        //List<T> result = new ArrayList();
        if (model != null) {
            where(model);
        }
        String sql = builder.buildSelect(this);
        return DB.select(sql , model != null ? model.getClass() : Map.class , builder.getBindData().toArray());

/*

        ResultSet rs = query(sql);
        if (rs == null) {
            return result;
        }
        T data = null;
        while (((data = fetchEntity(rs)) != null)) {
            result.add(data);
        }
        free();
        return result;*/
    }

    private String getFieldName(Field field) {
        Fields fields = field.getAnnotation(Fields.class);
        if (fields != null && !StringUtil.isNullOrEmpty(fields.value())) {
            return fields.value();
        }
        return field.getName();
    }


//    public String fetchSelectSql()
//    {
//        String sql = builder.buildSelect(this);
//        List<Object> bindData = builder.getBindData();
//    }
//
//    private String bindDataToSql(String sql , List<Object> bindData)
//    {
//        try {
//            PreparedStatement statement = getConn().prepareStatement(sql);
//            setBindData(statement , bindData);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//    }


    protected T getInstance(Class<T> superClass) {
        try {
            if (Map.class.isAssignableFrom(superClass))
            {
                return (T)new QMap();
            }else{
                return superClass.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    protected T getInstanceOfT() {
        if (model != null) {
            Class<T> d = (Class<T>) model.getClass();
            return getInstance(d);
        } else {
            ParameterizedType superClass = (ParameterizedType) this.getClass().getGenericSuperclass();
            Class<T> type = (Class<T>) superClass.getActualTypeArguments()[0];
            return getInstance(type);
        }
    }

    protected T getTemplate() {
        return getInstanceOfT();
    }

    /**
     * 根据ResultSet 获取数据行
     *
     * @param rs 结果集
     * @return 实例
     */
    public T fetchEntity(ResultSet rs) {
        T data = getTemplate();
        if (data instanceof Map) {
            return (T) fetch(rs);
        }
        if (rs == null) {
            return null;
        }

        try {
            Object o;
            o = DB.fetchEntity(rs, tableModel);
            return (T)o;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*try {
            if (rs.next()) {
                Field[] fields = data.getClass().getDeclaredFields();
                for (Field field : fields) {
                    try {
                        FieldInfoModel infoModel = tableModel.fieldInfo.get(field.getName());
                        if(infoModel == null )continue;
                        Object result = infoModel.getGetMethod().invoke(rs, infoModel.getName());

                        Fields annField = infoModel.getAnnField();
                        Class<?> type = field.getType();
                        Method method = infoModel.getSetMethod();

                        if (annField != null && annField.type() == FieldType.JSON) {
                            try {
                                String jsonData = String.valueOf(result);
                                // JSON 数据格式
                                if (Map.class.isAssignableFrom(type)) {
                                    Map map = JSON.parseObject(jsonData);
                                    method.invoke(data, map);
                                } else if (Collection.class.isAssignableFrom(type)) {
                                    Collection collection = JSON.parseArray(jsonData);
                                    method.invoke(data, collection);
                                } else {
                                    method.invoke(data, JSON.parseObject(jsonData, type));
                                }
                            } catch (JSONException e) {

                            }
                        } else {
                            method.invoke(data, result);
                        }
                    } catch (Exception sql) {
                        sql.printStackTrace();
                    }
                }
                return data;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        return null;
    }

    private String parseName(String name) {
        String c = toLineString(name);
        return c.substring(0, 1).toUpperCase() + c.substring(1);
    }


    public static String toLineString(String string) {

        StringBuilder stringBuilder = new StringBuilder();
        String[] str = string.split("_");
        for (String string2 : str) {
            if (stringBuilder.length() == 0) {
                stringBuilder.append(string2);
            } else {
                stringBuilder.append(string2.substring(0, 1).toUpperCase());
                stringBuilder.append(string2.substring(1));
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 根据ResultSet 获取数据行
     *
     * @param rs 结果集
     * @return Map结果
     */
    public Map fetch(ResultSet rs) {

        try {
            return DB.fetchMap(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
        /*QMap data = new QMap();
        if (rs == null) return null;
        try {
            if (rs.next()) {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String name = rsmd.getColumnName(i);
                    String value = rs.getString(i);
                    if (value == null || value.toLowerCase().equals("null")) {
                        value = "";
                    }
                    data.put(name, value);
                }

            } else {
                return null;
            }
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
        return data;*/
    }

    /*protected ArrayList resultSetList = new ArrayList();*/

    /**
     * 查询sql 语句并返回ResultSet，这个不需要释放，系统在释放时会自动释放
     *
     * @param sql 查询得sql 语句
     * @return 结果集
     *//*
    public ResultSet query(String sql) {
        return query(sql, builder.getBindData());
    }

    *//**
     * 查询sql 语句并返回ResultSet，这个不需要释放，系统在释放时会自动释放
     *
     * @param sql      sql 语句
     * @param bindData 绑定值
     * @return 结果集
     *//*
    public ResultSet query(String sql, List<Object> bindData) {
        try {
            Connection conn = Configuration.getConnectionConfig().getConn();
            PreparedStatement statement = conn.prepareStatement(sql);
            setBindData(statement, bindData);
            //Statement st = conn.createStatement();
            ResultSet rs = statement.executeQuery();
            DB.log(sql, bindData);
            resultSetList.add(rs);
            resultSetList.add(statement);
            return rs;
        } catch (SQLException e) {
            DB.log(e, sql);
        }
        return null;
    }

    private void setBindData(PreparedStatement statement, List bindData) throws SQLException {
        for (int i = 0; i < bindData.size(); i++) {
            Object data = bindData.get(i);
            int index = i + 1;
            if (data instanceof Integer) {
                statement.setInt(index, (Integer) data);
            } else if (data instanceof Float) {
                statement.setFloat(index, (Float) data);
            } else if (data instanceof Long) {
                statement.setLong(index, (Long) data);
            } else if (data instanceof Double) {
                statement.setDouble(index, (Double) data);
            } else if(data instanceof String){
                statement.setString(index , (String) data);
            } else {
                statement.setObject(index, data);
            }
        }
    }*/


    /**
     * 根据当前条件获取一行数据中的某个字段的值
     *
     * @param name 字段名
     * @return 值
     */
    public String value(String name) {
        if (!mOption.containsKey("field")) {
            field(name);
        }
        Map data = findMap();
        if (data.isEmpty()) {
            return "";
        }
        return String.valueOf(data.get(name));
    }

    /**
     * 设置SQL 分组
     *
     * @param nGroup 分组信息
     * @return 当前实例
     */
    public QueryWrapper<T> group(String nGroup) {
        getOptionArrayList("group").add(nGroup);
        return this;
    }

    /**
     * 设置 SQL 排序字段
     *
     * @param nOrder 字段名和排序信息
     * @return 当前实例
     */
    public QueryWrapper<T> order(String nOrder) {
        getOptionArrayList("order").add(nOrder);
        return this;
    }

    /**
     * 设置 SQL 排序字段
     *
     * @param nOrder 字段名
     * @param sort   排序名
     * @return 当前实例
     */
    public QueryWrapper<T> order(String nOrder, String sort) {
        getOptionArrayList("order").add(nOrder + " " + sort);
        return this;
    }

    /**
     * 设置 SQL 排序字段
     *
     * @param nOrder 字段名
     * @return 当前实例
     */
    public QueryWrapper<T> orderDesc(String nOrder) {
        getOptionArrayList("order").add(nOrder + " desc");
        return this;
    }

    /**
     * 设置 SQL 升序字段
     *
     * @param nOrder 字段名
     * @return 当前实例
     */
    public QueryWrapper<T> orderAsc(String nOrder) {
        getOptionArrayList("order").add(nOrder + " asc");
        return this;
    }


    /**
     * 设置SQL语句使用全连接 会生成如下：INNER JOIN table t on cond 的形式
     *
     * @param table 表名 as 别名
     * @param cond  条件
     * @return 当前实例
     */
    public QueryWrapper<T> joinInner(String table, String cond) {
        return join(table, cond, "INNER");
    }

    /**
     * 设置sql 语句使用右连接 会生成如下：RIGHT JOIN table t on cond 的形式
     *
     * @param table 表名 as 别名
     * @param cond  条件
     * @return 当前实例
     */
    public QueryWrapper<T> joinRight(String table, String cond) {
        return join(table, cond, "RIGHT");
    }

    /**
     * 设置sql 语句使用左连接 会生成如下：table t on cond 的形式
     *
     * @param table 表名 as 别名
     * @param cond  条件
     * @return 当前实例
     */
    public QueryWrapper<T> joinLeft(String table, String cond) {
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
    public QueryWrapper<T> join(String table, String cond, String type) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" ").append(type).append(" JOIN ").append(table).append(" ON ").append(cond);

        getOptionArrayList("join").add(buffer.toString());
        return this;
    }

    /**
     * 设置当前表的别名
     *
     * @param name 别名
     * @return 当前实例
     */
    public QueryWrapper<T> alias(String name) {
        mOption.put("alias", name);
        return this;
    }

    /**
     * 获取设置参数
     *
     * @param type 属性名
     * @return 属性得值
     */
    private HashMap getOptionHashMap(String type) {
        if (mOption.containsKey(type)) {
            return (HashMap) mOption.get(type);
        }
        HashMap map = new HashMap();
        mOption.put(type, map);
        return map;
    }

    /**
     * 获取设置参数
     *
     * @param type 属性名
     * @return 属性列表值
     */
    private ArrayList getOptionArrayList(String type) {
        if (mOption.containsKey(type)) {
            return (ArrayList) mOption.get(type);
        }
        ArrayList map = new ArrayList();
        mOption.put(type, map);
        return map;
    }

    /**
     * 设置SQL条件
     *
     * @param name sql条件
     * @return 当前实例
     */
    public QueryWrapper<T> where(String name) {
        HashMap list = new HashMap();
        list.put("where", name);
        getOptionArrayList("where").add(list);
        return this;
    }

    /**
     * 设置SQL条件 会自动写成 and name='value' 这样的形式
     *
     * @param name  字段名
     * @param value 条件值
     * @return 当前实例
     */
    public QueryWrapper<T> where(String name, Object value) {
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
    public QueryWrapper<T> where(String name, String eq, Object value) {
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
    public QueryWrapper<T> where(String name, String eq, Object Value, String connect) {
        HashMap list = new HashMap();
        list.put("name", name);
        list.put("exp", eq == null ? "=" : eq);
        list.put("value", Value == null ? "" : Value);
        list.put("connect", connect == null ? "and" : connect);

        getOptionArrayList("where").add(list);

        return this;
    }

    /**
     * 设置SQL条件 会自动写成 and field in(inArray) 这样的形式
     *
     * @param field   字段名
     * @param inArray 字符串得形式
     * @return 当前实例
     */
    public QueryWrapper<T> whereIn(String field, String inArray) {
        String[] arr = inArray.split(",");
        return where(field, "in", arr);
    }

    /**
     * 设置SQL条件 会自动写成 and field like inArray 这样的形式
     *
     * @param field   字段名
     * @param inArray 列表对象
     * @return 当前实例
     */
    public QueryWrapper<T> whereLike(String field, Object inArray) {
        return where(field, "like", inArray);
    }

    /**
     * 设置SQL条件 会自动写成 and field not like inArray 这样的形式
     *
     * @param field   字段名
     * @param inArray 字段值
     * @return 当前实例
     */
    public QueryWrapper<T> whereLikeNot(String field, Object inArray) {
        return where(field, "not like", inArray);
    }

    /**
     * 设置SQL条件 会自动写成 and field in(inArray1,inArray2) 这样的形式
     *
     * @param field   字段名
     * @param inArray 字段值
     * @return 当前实例
     */
    public QueryWrapper<T> whereIn(String field, Object inArray) {
        return where(field, "in", inArray);
    }

    /**
     * 设置SQL条件 会自动写成 and field not in(inArray1) 这样的形式
     *
     * @param field   字段名
     * @param inArray 字段值
     * @return 当前实例
     */
    public QueryWrapper<T> whereInNot(String field, Object inArray) {
        return where(field, "not in", inArray);
    }

    /**
     * 设置SQL条件 会自动写成 and field between inArray 这样的形式
     *
     * @param field   字段名
     * @param inArray 字段值
     * @return 当前实例
     */
    public QueryWrapper<T> whereBetween(String field, String inArray) {
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
    public QueryWrapper<T> whereBetween(String field, String start, String end) {
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
    public QueryWrapper<T> whereBetweenNot(String field, String inArray) {
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
    public QueryWrapper<T> whereBetweenNot(String field, String start, String end) {
        List<String> data = new ArrayList(2);
        data.add(start);
        data.add(end);
        return where(field, "not between", data);
    }

    /**
     * 执行插入语句
     *
     * @param sql 执行插入语句
     * @return 主键自增得id
     */
    private int executeInsert(String sql) {
        return DB.executeInsert(sql, builder.getBindData().toArray());
    }


    /**
     * 执行更新语句
     *
     * @param sql 更新得sql
     * @return 更新行数
     */
    private int executeUpdate(String sql) {
        return DB.executeUpdate(sql, builder.getBindData().toArray());
    }


    /**
     * 获取一页数据，并生成分页代码
     *
     * @param page 分页信息
     * @return 获取统计计算后得分页信息
     */
    public Collect<T> page(Collect<T> page) {
        QueryWrapper<T> c = null; //new QueryWrapper(getName());
        try {
            Class cla = this.getClass();
            c = (QueryWrapper<T>) cla.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        c.mOption.putAll(mOption);
        // 总长度
        long count = c.count();
        page.setTotalRows(count);

        this.limit(page.getFirstRow(), page.getPageSize());
        builder.setPage(true);
        List list = select();

        builder.setPage(false);
        page.addAll(list);

        return page;
    }

    /**
     * 获取当前option
     *
     * @return 当前设置得信息
     */
    public Map getOption() {
        return mOption;
    }

    /**
     * 获取某列得所有行
     *
     * @param field 字段名
     * @return 列表
     */
    public List<String> column(String field) {
        if (!mOption.containsKey("field")) {
            this.field(field);
        }

        QueryMap queryMap = new QueryMap(getName());
        queryMap.mOption.putAll(mOption);
        List<QMap> list = queryMap.select();
        List<String> result = new ArrayList();
        for (QMap map : list) {
            result.add(map.getString(field));
        }
        return result;
    }

    /**
     * 根据当前条件获取列数据,健对值的关系
     *
     * @param field 值
     * @param key   键
     * @return map 键对值
     */
    public Map column(String field, String key) {
        if (!mOption.containsKey("field")) {
            this.field(field);
            this.field(key);
        }

        QueryMap queryMap = new QueryMap(getName());
        queryMap.mOption.putAll(mOption);
        List<Map> list = queryMap.select();
        Map result = new LinkedHashMap();

        for (Map map : list) {
            result.put(map.get(key), map.get(field));
        }
        return result;
    }

    /**
     * 根据当前条件，获取一列的数据
     *
     * @param field 字段名
     * @return 列表
     */
    public List<String> getCol(String field) {
        return column(field);
    }


    /**
     * 根据当前条件获取列数据,健对值的关系
     *
     * @param field 值
     * @param key   键
     * @return map 键对值
     */
    public Map getColkey(String field, String key) {
        return column(field, key);
    }


}
