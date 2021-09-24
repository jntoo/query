package com.jntoo.db;


import com.alibaba.fastjson.JSON;
import com.jntoo.db.annotation.FieldType;
import com.jntoo.db.annotation.Fields;
import com.jntoo.db.annotation.HasOne;
import com.jntoo.db.build.Builder;
import com.jntoo.db.has.HasManyQuery;
import com.jntoo.db.has.HasOneQuery;
import com.jntoo.db.has.HasQuery;
import com.jntoo.db.model.*;
import com.jntoo.db.utils.*;
import sun.dc.pr.PRError;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 操作数据库链式执行
 * 目前只实现了部分方法，之后会继续完善该代码,让其支持实体类的数据获取
 * 使用方法：Query.make("表名称").where("字段名" , "条件符号","条件值").select()
 */
abstract public class QueryWrapperBase<T ,TS extends QueryWrapperBase> extends QueryOptions<TS> {
    protected String mName = "";
    //protected Map mOption = null;
    protected String pk = "id";
    protected String prefix = ""; // 设置表前缀
    protected Map mData = null;
    protected Builder builder = null;
    protected TableModel tableModel;
    protected T model;
    /*protected Options options;*/

    public QueryWrapperBase() {
        reset();
        model = getInstanceOfT();
        handlerClass();
    }

    /**
     * 构造Query
     *
     * @param name 表名
     */
    public QueryWrapperBase(String name) {
        reset();
        model = (T)new HashMap();
        setName(name);
    }

    /**
     * 构造Query
     *
     * @param cls 实体类得Class
     */
    public QueryWrapperBase(Class<T> cls) {
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
    public QueryWrapperBase(T cls) {
        reset();
        model = cls;
        handlerClass();
        Map map = entityToMap(cls);
        mData.putAll(map);
    }

    protected void handlerClass() {
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
     */
    @Override
    protected void reset() {
        super.reset();
        ConnectionConfig connectionConfig = Configuration.getConnectionConfig();
        if (connectionConfig == null) {
            throw new RuntimeException("not ConnectionConfig");
        }
        model = null;
        mName = "";
        //mOption = null;
        //mOption = new HashMap();
        mData = new HashMap();
        builder = Builder.make(this);
        prefix = Configuration.getPrefix();
    }


    /**
     * 设置一个字段自增
     *
     * @param field 自增得字段
     * @param step  自增步长
     * @return QueryWrapper实例
     */
    public TS inc(String field, int step) {
        if (step < 1) step = 1;
        ArrayList list = new ArrayList();
        list.add("inc");
        list.add(step);
        data(field, list);
        return (TS)this;
    }

    /**
     * 设置一个字段自减
     *
     * @param field 自增得字段
     * @param step  自增得步长
     * @return QueryWrapper实例
     */
    public TS dec(String field, int step) {
        if (step < 1) step = 1;
        ArrayList list = new ArrayList();
        list.add("dec");
        list.add(step);

        data(field, list);
        return (TS)this;
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
        if (!options.isWhere()) {
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

        String sql = builder.buildUpdate();
        executeUpdate(sql);
        return true;
    }

    protected void handlerAutoTimerField(List<String> fields) {
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

    protected void handlerAutoField(List<String> autoFields) {
        if (autoFields.size() > 0) {
            Map clone = new HashMap();
            clone.putAll(mData);

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


    protected Map entityToMap(T data) {
        if (data instanceof Map) {
            return (Map) data;
        } else {
            HashMap map = new HashMap();
            try {
                Class c = data.getClass();
                List<FieldInfoModel> fieldInfos = tableModel.getFieldInfos();//c.getDeclaredFields();
                for (FieldInfoModel fieldInfo : fieldInfos) {
                    Field field = fieldInfo.getField();
                    //FieldInfoModel fieldInfo = tableModel.getFieldInfo(field.getName());
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
    public TS data(Map value) {
        Map<String, Object> datas = value;
        for (Map.Entry<String, Object> entry : datas.entrySet()) {
            data(entry.getKey(), entry.getValue());
        }
        return (TS)this;
    }

    /**
     * 向当前query 写入data
     *
     * @param name  字段名
     * @param value 属性值
     * @return 当前实例
     */
    public TS data(String name, Object value) {
        String field = name;
        if (tableModel != null) {
            FieldInfoModel fieldInfo = tableModel.getFieldInfo(field);
            if (fieldInfo != null) {
                field = fieldInfo.getName();
            }
        }

        mData.put(field, value);
        return (TS)this;
    }

    /**
     * 向当前query 写入data
     *
     * @param name  字段名
     * @param value 属性值
     * @return 当前实例
     */
    public TS data(String name, boolean value) {
        mData.put(name, value ? 1 : 0);
        return (TS)this;
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
        String sql = builder.buildInsert( replace);
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
     * 设置表名称
     *
     * @param name 表名称，不带前缀
     * @return 当前实例
     */
    public TS setName(String name) {
        mName = name;
        return (TS)this;
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
     * 根据ID 获取一行数据
     *
     * @param id 主键id
     * @return ${T}实例信息
     */
    public T find(Object id) {
        where(getPk(), id);
        return find();
    }

    /**
     * 根据当前条件获取一行数据
     *
     * @return ${T}实例信息
     */
    public T find() {
        //limit(1);
        String sql = builder.buildSelect();

        T result = (T)DB.find(sql , model != null ? model.getClass() : Map.class , builder.getBindData().toArray());
        if(result == null){
            return null;
        }
        if(!(result instanceof Map))
        {
            if( options.isHasQuery() )
            {
                List<T> hasList = new ArrayList(3);
                hasList.add(result);
                setHasQuery(hasList , options.getHasQuery());
            }
            if(tableModel != null && tableModel.getHasQuery().size() > 0)
            {
                List<T> hasList = new ArrayList(3);
                hasList.add(result);
                setHasQuery(hasList , tableModel.getHasQuery());
            }
        }

        return result;
    }

    protected void setHasQuery( List<T> list , List<HasQuery> queryList )
    {
        if (list.size() > 0){
            T result = list.get(0);

            Class resultClass = result.getClass();
            ArrayList values = new ArrayList(list.size());

            // 有带入
            for (HasQuery has : queryList)
            {
                Class<?> target = has.getTarget();
                TableModel targetModel = TableManageUtils.getTable(target);

                QueryWrapper queryWrapper = DB.name(target);
                queryWrapper.setOptions(has.getOptions());

                Field targetField;
                try {
                    Field field = resultClass.getDeclaredField(has.getForeignKey());
                    field.setAccessible(true);
                    for (T t : list) {
                        values.add(field.get(t));
                    }

                    targetField = resultClass.getDeclaredField(has.getField());
                    Field targetLocalKeyField = targetModel.getFieldInfo(has.getLocalKey()).getField();

                    String targetFieldName = targetModel.getFieldInfo(has.getLocalKey()).getName();
                    if (values.size() == 1){
                        queryWrapper.where(targetFieldName , values.get(0));
                    }else{
                        queryWrapper.whereIn(targetFieldName , values);
                    }

                    if(has instanceof HasOneQuery)
                    {
                        List selectList = queryWrapper.select();
                        Map keyToData = new HashMap();
                        for (Object o : selectList) {
                            // 将这些数据写成Map 格式
                            Object key = Convert.toStr(TableManageUtils.getFieldValue( targetLocalKeyField , o ));
                            keyToData.put(key  , o);
                        }
                        for (T t : list) {
                            Object key = Convert.toStr(TableManageUtils.getFieldValue( field , t ));
                            Object value = keyToData.get(key);
                            TableManageUtils.setFieldValue(targetField , t , value);
                        }
                    }else if (has instanceof HasManyQuery) {
                        List selectList = queryWrapper.select();
                        Map<Object , List> keyToData = new HashMap();
                        for (Object o : selectList) {
                            // 将这些数据写成Map 格式
                            Object key = Convert.toStr(TableManageUtils.getFieldValue( targetLocalKeyField , o ));
                            if( !keyToData.containsKey(key) )
                            {
                                keyToData.put(key , new ArrayList());
                            }
                            keyToData.get(key).add(o);
                        }
                        for (T t : list) {
                            Object key = Convert.toStr(TableManageUtils.getFieldValue( field , t ));
                            TableManageUtils.setFieldValue(targetField , t , keyToData.get(key));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                values.clear();
            }

        }
    }


    /**
     * 根据当前条件获取一行数据
     *
     * @return 获取Map
     */
    protected Map findMap() {
        //limit(1);
        String sql = builder.buildSelect();
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
        List source = options.getField();
        if (options.isField()) {
            options.setField(new ArrayList());
        }
        options.addField(field);
        Map data = findMap();
        options.setField(source);
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
            if (!StringUtil.isNullOrEmpty(options.getAlias())) {
                field = "count(" + options.getAlias() + ".id) count";
            } else {
                field = "count(*) count";
            }
        } else {
            field = "count(" + field + ") count";
        }

        List sourceField = options.getField();
        List sourceOrder = options.getOrder();
        List sourceGroup = options.getGroup();
        LimitModel limitModel = options.getLimit();

        options.setField(new ArrayList(1));
        options.setOrder(new ArrayList(1));
        options.setGroup(new ArrayList(1));
        options.setLimit((LimitModel) null);

        options.addField(field);
        Map data = findMap();
        options.setField(sourceField);
        options.setOrder(sourceOrder);
        options.setGroup(sourceGroup);
        options.setLimit(limitModel);

        if (data != null && data.containsKey("count")) {
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
        if (!options.isWhere()) {
            return -1;
        }
        String sql = this.builder.buildDelete();
        return executeUpdate(sql);
    }

    public TS where(T data) {
        if (data == null) {
            return (TS)this;
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
        return (TS)this;
    }

    /**
     * 根据当前条件获取数据集
     *
     * @return 列表行
     */
    public List select() {
        if (model != null) {
            where(model);
        }

        String sql = builder.buildSelect();

        List selectList = DB.select(sql , model != null ? model.getClass() : Map.class , builder.getBindData().toArray());
        if(selectList.size() == 0)
        {
            return selectList;
        }
        if(!(selectList.get(0) instanceof Map)){
            if( options.isHasQuery() )
            {
                setHasQuery(selectList , options.getHasQuery());
            }
            if(tableModel != null && tableModel.getHasQuery().size() > 0)
            {
                setHasQuery(selectList , tableModel.getHasQuery());
            }
        }


        return selectList;
    }

    /**
     * 根据当前条件获取数据集
     *
     * @return 列表行
     */
    public List selectMap() {
        if (model != null) {
            where(model);
        }
        String sql = builder.buildSelect();
        return DB.select(sql , Map.class , builder.getBindData().toArray());
    }

    protected String getFieldName(Field field) {
        Fields fields = field.getAnnotation(Fields.class);
        if (fields != null && !StringUtil.isNullOrEmpty(fields.value())) {
            return fields.value();
        }
        return field.getName();
    }

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
     * @return Map结果
     */
    public Map fetch(ResultSet rs) {
        try {
            return DB.fetchMap(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据当前条件获取一行数据中的某个字段的值
     *
     * @param name 字段名
     * @return 值
     */
    public String value(String name) {
        if (!options.isField()) {
            field(name);
        }
        Map data = findMap();
        if (data.isEmpty()) {
            return "";
        }
        return String.valueOf(data.get(name));
    }

    /**
     * 执行插入语句
     *
     * @param sql 执行插入语句
     * @return 主键自增得id
     */
    protected int executeInsert(String sql) {
        return DB.executeInsert(sql, builder.getBindData().toArray());
    }


    /**
     * 执行更新语句
     *
     * @param sql 更新得sql
     * @return 更新行数
     */
    protected int executeUpdate(String sql) {
        return DB.executeUpdate(sql, builder.getBindData().toArray());
    }


    /**
     * 获取一页数据，并生成分页代码
     *
     * @param page 分页信息
     * @return 获取统计计算后得分页信息
     */
    public Collect<T> page(Collect<T> page) {
        /*TS c = null;*/ //new QueryWrapper(getName());
        /*try {
            Class cla = this.getClass();
            cla.getConstructor(String.class);
            c = (TS) cla.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }*/
        //List currentFields = options.getField();
        //c.setOptions(options);
        // 总长度

        long count = page.getTotalRows();
        if(count == 0){
            count = count();
            builder.clearBindData();
            page.setTotalRows(count);
        }
        this.limit(page.getFirstRow(), page.getPageSize());
        builder.setPage(true);
        List list = select();
        builder.setPage(false);
        page.addAll(list);
        return page;
    }

    /**
     * 获取某列得所有行
     *
     * @param field 字段名
     * @return 列表
     */
    public List<String> column(String field) {
        if (!(options.isField())){
            this.field(field);
        }

        List<QMap> list = selectMap();
        List<String> result = new ArrayList();
        for (QMap map : list) {
            result.add(map.getString(field));
        }
        return result;
    }

    /**
     * 根据当前条件获取列数据,健对值的关系
     *
     * @param field 值 如果带有,逗号则会写入整个个对象
     * @param key   键
     * @return map 键对值 看field 是否带有 , 逗号分隔字段，如果有则是全部否则就是键对值
     */
    public Map column(String field, String key) {
        boolean isList = field.indexOf(",") != -1;

        if (!(options.isField())){
            this.field(field);
            this.field(key);
        }

        List<Map> list = selectMap();
        Map result = new LinkedHashMap();

        for (Map map : list) {
            if(isList){
                result.put(map.get(key), map);
            } else {
                result.put(map.get(key), map.get(field));
            }
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
