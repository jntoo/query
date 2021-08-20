package com.jntoo.db;


import com.jntoo.db.annotation.FieldType;
import com.jntoo.db.annotation.Fields;
import com.jntoo.db.annotation.Table;
import com.jntoo.db.build.Builder;

import com.jntoo.db.model.QMap;
import com.jntoo.db.utils.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.*;

import static java.awt.font.TextAttribute.UNDERLINE;

/**
 * 操作数据库链式执行
 * 目前只实现了部分方法，之后会继续完善该代码,让其支持实体类的数据获取
 * 使用方法：Query.make("表名称").where("字段名" , "条件符号","条件值").select()
 *
 */
public class QueryWrapper<T> {
    protected String mName = "";
    protected HashMap mOption = null;
    protected String pk = "id";
    protected HashMap mData = null;
    protected Builder builder = null;
    private static ConnectionConfig connectionConfig;


    T  m;

    public static ConnectionConfig getConnectionConfig() {
        return connectionConfig;
    }

    public static void setConnectionConfig(ConnectionConfig config) {
        connectionConfig = config;
    }


    public static HashMap tableFields = new HashMap();
    public QueryWrapper()
    {
        reset();
        handlerClass();
    }


    /**
     * 构造Query
     * @param name
     */
    public QueryWrapper(String name)
    {
        reset();
        setName(name);
    }

    /**
     * 构造Query
     * @param cls
     */
    public QueryWrapper(Class<T> cls)
    {
        reset();
        try {
            m = cls.newInstance();
            handlerClass();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构造Query
     * @param cls
     */
    public QueryWrapper(T cls)
    {
        reset();
        m = cls;
        handlerClass();
        Map map = entityToMap(cls);
        mData.putAll(map);
    }

    private void handlerClass()
    {
        T instance = getInstanceOfT();
        if(!(instance instanceof Map))
        {
            Class cls = instance.getClass();
            Table table = (Table) cls.getAnnotation(Table.class);
            if(table != null && !"".equals(table.value())){
                setName(table.value());
            }else{
                setName(parseClassToName(cls));
            }

            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                Fields fieldsAnnotation = field.getAnnotation(Fields.class);
                if(fieldsAnnotation != null){
                    if(fieldsAnnotation.type() == FieldType.PK || fieldsAnnotation.type() == FieldType.PK_AUTO)
                    {
                        setPk(fieldsAnnotation.value()!=null && !fieldsAnnotation.value().equals("") ? fieldsAnnotation.value() : field.getName());
                    }
                }
            }
        }
    }

    protected String parseClassToName(Class cls)
    {
        String clsName = cls.getSimpleName();
        return StringUtil.camelToUnderline(clsName,1);
    }




    /**
     * 重置并初始化数据
     * @return
     */
    protected QueryWrapper<T> reset()
    {
        if(connectionConfig == null){
            connectionConfig = Configuration.getConnectionConfig();
            if(connectionConfig == null)
            {
                throw new RuntimeException("not ConnectionConfig");
            }
        }
        mName = "";
        mOption = null;
        mOption = new HashMap();
        mData = new HashMap();
        builder = Builder.make(connectionConfig.getConn());

        if(tableFields == null)
        {
            tableFields = new HashMap();
        }
        return this;
    }

    /**
     * 设置一个字段自增
     * @param field
     * @param step
     * @return
     */
    public QueryWrapper<T> inc(String field , int step)
    {
        if(step<1)step = 1;
        ArrayList list = new ArrayList();
        list.add("inc");
        list.add(step);
        mData.put(field , list);
        return this;
    }
    /**
     * 设置一个字段自减
     * @param field
     * @param step
     * @return
     */
    public QueryWrapper<T> dec(String field , int step)
    {
        if(step<1)step = 1;
        ArrayList list = new ArrayList();
        list.add("dec");
        list.add(step);
        mData.put(field , list);
        return this;
    }
    /**
     * 马上更新数据字段自增1
     * @param field
     * @return
     */
    public boolean setInc(String field)
    {
        return setInc(field ,1);
    }
    /**
     * 马上更新数据字段自增step
     * @param field
     * @param step
     * @return
     */
    public boolean setInc(String field , String step)
    {
        return inc(field , Integer.valueOf(step).intValue()).update();
    }
    /**
     * 马上更新数据字段自增step
     * @param field
     * @param step
     * @return
     */
    public boolean setInc(String field , int step)
    {
        return inc(field , step).update();
    }
    /**
     * 马上更新数据字段自减1
     * @param field
     * @return
     */
    public boolean setDec(String field )
    {
        return setDec(field , 1);
    }
    /**
     * 马上更新数据字段自减step
     * @param field
     * @param step
     * @return
     */
    public boolean setDec(String field , String step)
    {
        return dec(field , Integer.valueOf(step).intValue()).update();
    }
    /**
     * 马上更新数据字段自减step
     * @param field
     * @param step
     * @return
     */
    public boolean setDec(String field , int step)
    {
        return dec(field , step).update();
    }
    /**
     * 设置某字段为某个值，并更新
     * @param field
     * @param step
     * @return
     */
    public boolean setField(String field , Object step)
    {
        mData.put(field , step);
        return update();
    }

    /**
     * 获取当前写入的data
     * @return
     */
    public HashMap getData()
    {
        return mData;
    }

    /**
     * 更新当前数据
     * @return
     */
    public boolean update()
    {
        return update(null);
    }

    /**
     * 更新当前数据加写入的data
     * @param updateData
     * @return
     */
    public boolean update( T updateData )
    {
        if(updateData != null){
            mData.putAll(entityToMap(updateData));
        }
        if(getOptionArrayList("where").size() == 0){
            if(!mData.containsKey(getPk())) // 没有条件，不更新
            {
                return false;
            }
            where(getPk() , mData.get(getPk()));
        }
        String sql = builder.buildUpdate(this);
        executeUpdate(sql);
        return true;
    }


    private Map entityToMap(T data)
    {
        if(data instanceof Map)
        {
            return (Map)data;
        }else{
            HashMap map = new HashMap();
            try
            {
                Class c = data.getClass();
                Field[] fields = c.getDeclaredFields();
                for (Field field : fields) {
                    String name = getFieldName(field);//.getName();
                    boolean isAcc = field.isAccessible();
                    if (!isAcc)
                    {
                        field.setAccessible(true);
                    }
                    Object value = field.get(data);
                    if (!isAcc)
                    {
                        field.setAccessible(false);
                    }
                    if(value!=null){
                        map.put(name , value);
                    }
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return map;
        }

    }


    /**
     * 向query 写入data
     * @param data
     * @return
     */
    public QueryWrapper<T> data(Map data)
    {
        mData.putAll(data);
        return this;
    }

    /**
     * 向当前query 写入data
     * @param name
     * @param value
     * @return
     */
    public QueryWrapper<T> data(String name , Object value)
    {
        mData.put(name , value);
        return this;
    }

    /**
     * 向当前query 写入data
     * @param name
     * @param value
     * @return
     */
    public QueryWrapper<T> data(String name , boolean value)
    {
        mData.put(name , value ? 1 : 0);
        return this;
    }

    public int insert()
    {
        return insert(null , false);
    }

    /**
     * 插入数据
     * @param insertData
     * @return
     */
    public int insert(T insertData ){ return insert(insertData , false); }

    /**
     * 插入数据
     * @param insertData
     * @param replace
     * @return
     */
    public int insert(T insertData , boolean replace)
    {
        if(insertData != null){
            mData.putAll(entityToMap(insertData));
        }
        String sql = builder.buildInsert(this , replace);
        int id = executeInsert(sql);
        if(insertData instanceof Map)
        {
            ((Map) insertData).put(getPk() , id);
        }else{
            try {
                String pk = getPk();
                String methodName = "set"+ StringUtil.firstCharUpper(pk);// pk.substring(0,1).toUpperCase()+pk.substring(1);
                if(insertData != null)
                {
                    Method setId = insertData.getClass().getMethod(methodName , Integer.class);
                    setId.invoke(insertData , id);
                }
                if(m != null){
                    Method setId = m.getClass().getMethod(methodName , Integer.class);
                    setId.invoke(m , id);
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

    /*protected Class entity = null;
    public DaoModel<T> setEntity(Class c)
    {
        entity = c;
        return this;
    }*/

    /**
     * 获取当前自增字段名称
     * @return
     */
    public String getPk() {
        return pk;
    }

    /**
     * 设置自增字段名
     * @param pk
     */
    public void setPk(String pk) {
        this.pk = pk;
    }

    /**
     * 尚未实现该代码，获取表的数据
     */
    protected void finalize()
    {

        //Statement st = conn.createStatement();
        //System.out.print(sql);
        //ResultSet rs
        //super.finalize();

        free();
    }

    /**
     * 释放资源
     */
    public void free()
    {
        // 释放rs
        for(int i=0;i<resultSetList.size();i++){
            Object os = resultSetList.get(i);
            try{
                if(os instanceof Statement){
                    Statement st = ((Statement) os);
                    st.close();
                }else if(os instanceof ResultSet){
                    ((ResultSet) os).close();
                }
            }catch (SQLException e){
            }
        }
        resultSetList.clear();
    }

    /**
     * 设置表名称
     * @param name
     * @return
     */
    public QueryWrapper<T> setName(String name)
    {
        mName = name;
        return this;
    }

    /**
     * 获取表名称
     * @return
     */
    public String getName()
    {
        return mName;
    }

    /**
     * 设置属性
     * @param name
     * @param value
     * @return
     */
    public QueryWrapper<T> setAttribute(String name , Object value)
    {
        getOptionHashMap("data").put(name , value);
        return this;
    }
    /**
     * 获取属性
     * @param name
     * @return
     */
    public Object getAttribute(String name)
    {
        return getOptionHashMap("data").get(name);
    }

    /**
     * 设置字段为 获取所有字段
     * @return
     */
    public QueryWrapper<T> field()
    {
        return field("*");
    }

    /**
     * 设置字段，可以用","逗号隔开多个
     * @param field
     * @return
     */
    public QueryWrapper<T> field(String field)
    {
        getOptionArrayList("field").add(field);
        return this;
    }

    /**
     * 设置表
     * @param nTable
     * @return
     */
    public QueryWrapper<T> table(String nTable)
    {
        getOptionArrayList("table").add(nTable);
        return this;
    }
    /**
     * 设置表
     * @param nTable
     * @return
     */
    public QueryWrapper<T> table(String nTable , String alias)
    {
        getOptionArrayList("table").add(nTable+" "+alias);
        return this;
    }
    /**
     * 设置行数
     * @param nLimit
     * @return
     */
    public QueryWrapper<T> limit(long nLimit)
    {
        //getOptionHashMap("limit").put("limit" , String.valueOf(nLimit));
        return limit(String.valueOf(nLimit));
    }

    /**
     * 设置起始行和行数
     * @param offset
     * @param nLimit
     * @return
     */
    public QueryWrapper<T> limit(long offset , long nLimit)
    {
        return limit(String.valueOf(offset) , String.valueOf(nLimit));
    }

    /**
     * 设置是否锁表
     * @param lock
     * @return
     */
    public QueryWrapper<T> lock(boolean lock )
    {
        return this.lock(lock ? " FOR UPDATE " : "");
    }

    /**
     * 设置锁表代码
     * @param lock
     * @return
     */
    public QueryWrapper<T> lock(String lock)
    {
        getOption().put("lock" , lock);
        return this;
    }

    /**
     * 设置行数，字符串形式
     * @param nLimit
     * @return
     */
    public QueryWrapper<T> limit(String nLimit)
    {
        if(nLimit.indexOf(",") != -1){
            String[] list = nLimit.split(",");
            return limit(list[0] , list[1]);
        }
        getOptionHashMap("limit").put("limit" , nLimit);
        return this;
    }

    /**
     * 设置起始行和行数
     * @param offset
     * @param nLimit
     * @return
     */
    public QueryWrapper<T> limit(String offset , String nLimit)
    {
        HashMap map = getOptionHashMap("limit");
        map.put("limit" , nLimit);
        map.put("offset" , offset);
        return this;
    }

    /**
     * 根据ID 获取一行数据
     * @param id
     * @return
     */
    public T find(Object id)
    {
        where(pk , id);
        return find();
    }

    /**
     * 根据当前条件获取一行数据
     * @return
     */
    public T find()
    {
        //limit(1);
        String sql = builder.buildSelect(this);
        ResultSet rs = query(sql);
        return (T)fetchEntity(rs);
    }

    /**
     * 根据当前条件获取一行数据
     * @return
     */
    private Map findMap()
    {
        //limit(1);
        String sql = builder.buildSelect(this);
        ResultSet rs = query(sql);
        return fetch(rs);
    }

    /**
     * 生成统计计算语句
     * @param f
     * @param func
     * @return
     */
    protected double total(String f , String func)
    {
        String ifnull = builder.parseIfNull(func+"("+f+")" , "0");
        String field = ifnull+" count";
        if(mOption.containsKey("field")){
            getOptionArrayList("field").clear();
        }
        getOptionArrayList("field").add(field);
        Map data = findMap();
        if(data.containsKey("count")){
            String count = data.get("count").toString();
            return Double.valueOf(count).doubleValue();
        }
        return 0;
    }

    /**
     * 求某字段和
     * @param field
     * @return
     */
    public double sum(String field)
    {
        return total(field , "SUM");
    }

    /**
     * 求某字段的平均值
     * @param field
     * @return
     */
    public double avg(String field)
    {
        return total(field , "AVG");
    }

    /**
     * 求最大值
     * @param field
     * @return
     */
    public double max(String field){
        return total(field , "MAX");
    }

    /**
     * 求最小值
     * @param field
     * @return
     */
    public double min(String field)
    {
        return total(field , "MIN");
    }

    /**
     * 求数据行数
     * @return
     */
    public long count()
    {
        return count(null);
    }
    /**
     * 根据字段名求数据行数
     * @return
     */
    public long count( String field )
    {
        if(field == null){
            if(mOption.containsKey("alias")){
                field = "count("+mOption.get("alias")+".id) count";
            }else{
                field = "count(*) count";
            }
        }else{
            field = "count("+field+") count";
        }
        if(mOption.containsKey("field")){
            mOption.put("field" , new ArrayList());
            //getOptionArrayList("field").clear();
        }
        if(mOption.containsKey("order")){
            mOption.remove("order");
        }
        getOptionArrayList("field").add(field);
        Map data = findMap();
        if(data.containsKey("count")){
            return Long.valueOf((String)data.get("count")).longValue();
        }
        return 0;
    }


    /**
     * 根据id 删除数据
     * @param id
     * @return
     */
    public long delete(Object id)
    {
        if(id instanceof List){
            where(getPk() ,"in", id);
        }else if(id instanceof String){
            String idObject = (String)id;
            if(idObject.indexOf(",")!=-1){
                where(getPk() , "in" , idObject);
            }else{
                where(getPk() , id);
            }
        }else{
            where(getPk() , id);
        }

        return delete();
    }


    /**
     * 根据当前条件删除数据，如果没有条件则不执行删除
     * @return
     */
    public long delete()
    {
        if(!mOption.containsKey("where")){
            return -1;
        }
        String sql = this.builder.buildDelete(this);
        return executeUpdate(sql);
    }

    public QueryWrapper<T> where(T data)
    {
        if(data == null){
            return this;
        }
        if(data instanceof Map){
            Map<String,Object> map = (Map) data;
            for (Map.Entry<String,Object> o : map.entrySet()) {
                where(o.getKey() , o.getValue());
            }
        }else{
            try {
                Field[] fields = data.getClass().getDeclaredFields();
                for (Field field : fields) {
                    String name = field.getName();
                    boolean isAcc = field.isAccessible();
                    if (!isAcc) field.setAccessible(true);
                    Object value = field.get(m);
                    if (!isAcc) field.setAccessible(false);
                    if(value!=null){
                        where(getFieldName(field) , value);
                    }
                }
            }catch (Exception e){

            }
        }
        return this;
    }

    /**
     * 根据当前条件获取数据集
     * @return
     */
    public List<T> select()
    {
        List<T> result = new ArrayList();
        if(m != null)
        {
            where(m);
        }

        String sql = builder.buildSelect(this);
        ResultSet rs = query(sql);
        if (rs == null) {
            return result;
        }
        T data = null;
        while( ((data = fetchEntity(rs)) != null) ){
            result.add(data);
        }
        return result;
    }

    private String getFieldName(Field field)
    {
        Fields fields = field.getAnnotation(Fields.class);
        if(! StringUtil.isNullOrEmpty(fields.value()) )
        {
            return fields.value();
        }
        return field.getName();
    }

    /**
     * 获取查询语句
     * @return
     */
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



    protected T getInstance( Class<T> superClass)
    {
        try {
            return superClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    protected T getInstanceOfT()
    {
        if(m != null){
            Class<T> d = (Class<T>) m.getClass();
            return getInstance(d);
        } else {
            ParameterizedType superClass = (ParameterizedType) this.getClass().getGenericSuperclass();
            Class<T> type = (Class<T>) superClass.getActualTypeArguments()[0];
            return getInstance(type);
        }


    }

    protected T getTemplate()
    {
        return getInstanceOfT();
    }

    /**
     * 根据ResultSet 获取数据行
     * @param rs
     * @return
     */
    public T fetchEntity(ResultSet rs)
    {
        T data = getTemplate();
        if(data instanceof Map)
        {
            return (T)fetch(rs);
        }

        if(rs == null){
            return null;
        }
        try {
            if(rs.next()){
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for(int i=1;i<=columnCount;i++){
                    String name = rsmd.getColumnName(i);
                    String value = rs.getString(i);
                    if(value == null || value.toLowerCase().equals("null")){
                        value = "";
                    }
                    Field field;
                    try{
                        field = data.getClass().getDeclaredField(name);
                    }catch (Exception e)
                    {
                        //e.printStackTrace();
                        continue;
                    }

                    Class<?> type = field.getType();
                    if(type == String.class)
                    {
                        Method method = data.getClass().getMethod("set"+parseName(name) , String.class);
                        method.invoke(data , value);
                    }else if (type == Integer.class)
                    {
                        Method method = data.getClass().getMethod("set"+parseName(name) , Integer.class);
                        method.invoke(data , rs.getInt(i));
                    }else if(type == Double.class){
                        Method method = data.getClass().getMethod("set"+parseName(name) , Double.class);
                        method.invoke(data , rs.getDouble(i));
                    }else if(type == Float.class)
                    {
                        Method method = data.getClass().getMethod("set"+parseName(name) , Float.class);
                        method.invoke(data , rs.getFloat(i));
                    }else if(type == Boolean.class)
                    {
                        Method method = data.getClass().getMethod("set"+parseName(name) , Boolean.class);
                        method.invoke(data , rs.getBoolean(i));
                    }else if(type == Character.class)
                    {
                        throw new Exception("not Character Type");
                    }

                    //data.put(name , value);
                }
                return data;
            }
        }catch (Exception sql){
            sql.printStackTrace();
        }
        return null;
    }

    private String parseName( String name  )
    {
        String c = toLineString(name);
        return c.substring(0,1).toUpperCase()+c.substring(1);
    }


    public static String toLineString(String string) {

        StringBuilder stringBuilder = new StringBuilder();
        String[] str = string.split("_");
        for (String string2 : str) {
            if(stringBuilder.length()==0){
                stringBuilder.append(string2);
            }else {
                stringBuilder.append(string2.substring(0, 1).toUpperCase());
                stringBuilder.append(string2.substring(1));
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 根据ResultSet 获取数据行
     * @param rs
     * @return
     */
    public HashMap fetch(ResultSet rs)
    {
        HashMap data = new HashMap();
        if(rs == null)return null;
        try {
            if(rs.next()){
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for(int i=1;i<=columnCount;i++){
                    String name = rsmd.getColumnName(i);
                    String value = rs.getString(i);
                    if(value == null || value.toLowerCase().equals("null")){
                        value = "";
                    }
                    data.put(name , value);
                }
            }else{
                return null;
            }
        }catch (SQLException sql){
            sql.printStackTrace();
        }
        return data;
    }

    protected ArrayList resultSetList = new ArrayList();

    /**
     * 查询sql 语句并返回ResultSet，这个不需要释放，系统在释放时会自动释放
     * @param sql
     * @return
     */
    public ResultSet query(String sql)
    {

        return query(sql , builder.getBindData());
        /*try {
            Connection conn = this.getConn();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            System.out.println(sql);
            resultSetList.add(rs);
            resultSetList.add(st);
            return rs;
        }catch (SQLException e){
            DB.log(e , sql);
        }
        return null;*/
    }

    /**
     * 查询sql 语句并返回ResultSet，这个不需要释放，系统在释放时会自动释放
     * @param sql
     * @return
     */
    public ResultSet query(String sql , List<Object> bindData)
    {
        try {

            Connection conn = this.getConn();
            PreparedStatement statement = conn.prepareStatement(sql);
            setBindData(statement , bindData);
            //Statement st = conn.createStatement();
            ResultSet rs = statement.executeQuery();

            DB.log(sql,bindData);

            resultSetList.add(rs);
            resultSetList.add(statement);
            return rs;
        }catch (SQLException e){
            DB.log(e , sql);
        }
        return null;
    }

    private void setBindData(PreparedStatement statement , List bindData) throws SQLException {
        for (int i=0;i<bindData.size();i++)
        {
            Object data = bindData.get(i);
            int index = i+1;
            if(data instanceof Integer)
            {
                statement.setInt(i+1 , (Integer) data);
            }else if(data instanceof Float){
                statement.setFloat(index , (Float) data);
            }else if(data instanceof Double){
                statement.setDouble(index , (Double) data);
            }else {
                statement.setObject(index , data);
            }
        }
    }



    /**
     * 根据当前条件获取一行数据中的某个字段的值
     * @param name
     * @return
     */
    public String value(String name)
    {
        if(!mOption.containsKey("field")){
            field(name);
        }
        Map data = findMap();
        if(data.isEmpty()){
            return "";
        }
        return String.valueOf(data.get(name));
    }

    /**
     * 设置SQL 分组
     * @param nGroup
     * @return
     */
    public QueryWrapper<T> group(String nGroup)
    {
        getOptionArrayList("group").add(nGroup);
        return this;
    }

    /**
     * 设置 SQL 排序字段
     * @param nOrder
     * @return
     */
    public QueryWrapper<T> order(String nOrder)
    {
        getOptionArrayList("order").add(nOrder);
        return this;
    }

    /**
     * 设置SQL语句使用全连接 会生成如下：INNER JOIN table t on cond 的形式
     * @param table
     * @param cond 条件
     * @return
     */
    public QueryWrapper<T> joinInner(String table , String cond)
    {
        return join(table , cond , "INNER");
    }

    /**
     * 设置sql 语句使用右连接 会生成如下：RIGHT JOIN table t on cond 的形式
     * @param table
     * @param cond
     * @return
     */
    public QueryWrapper<T> joinRight(String table , String cond)
    {
        return join(table , cond , "RIGHT");
    }
    /**
     * 设置sql 语句使用左连接 会生成如下：table t on cond 的形式
     * @param table
     * @param cond
     * @return
     */
    public QueryWrapper<T> joinLeft(String table , String cond)
    {
        return join(table , cond , "LEFT");
    }

    /**
     * 设置sql 语句使用右连接 会生成如下：type JOIN table t on cond 的形式
     * @param table
     * @param cond
     * @param type 跨不会类型
     * @return
     */
    public QueryWrapper<T> join(String table , String cond , String type)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" ").append(type).append(" JOIN ").append(table).append(" ON ").append(cond);

        getOptionArrayList("join").add(buffer.toString());
        return this;
    }

    /**
     * 设置当前表的别名
     * @param name
     * @return
     */
    public QueryWrapper<T> alias(String name)
    {
        mOption.put("alias" , name);
        return this;
    }

    /**
     * 获取设置参数
     * @param type
     * @return
     */
    private HashMap getOptionHashMap(String type)
    {
        if(mOption.containsKey(type)){
            return (HashMap) mOption.get(type);
        }
        HashMap map = new HashMap();
        mOption.put(type , map);
        return map;
    }

    /**
     * 获取设置参数
     * @param type
     * @return
     */
    private ArrayList getOptionArrayList(String type)
    {
        if(mOption.containsKey(type)){
            return (ArrayList) mOption.get(type);
        }
        ArrayList map = new ArrayList();
        mOption.put(type , map);
        return map;
    }

    /**
     * 设置SQL条件
     * @param name
     * @return
     */
    public QueryWrapper<T> where(String name)
    {
        HashMap list = new HashMap();
        list.put("where" , name);
        getOptionArrayList("where").add(list);
        return this;
    }

    /**
     * 设置SQL条件 会自动写成 and name='value' 这样的形式
     * @param name 字段名
     * @param value 条件值
     * @return
     */
    public QueryWrapper<T> where(String name , Object value)
    {
        return where(name , null , value ,null);
    }

    /**
     * 设置SQL条件 会自动写成 and name eq 'value' 这样的形式
     * @param name
     * @param eq   符号，可以写成：=、>、>=、<、<=、eq、neq、gt、egt、lt、elt
     * @param value
     * @return
     */
    public QueryWrapper<T> where(String name , String eq, Object value)
    {
        return where(name , eq , value ,null);
    }

    /**
     * 设置SQL条件 会自动写成 and name eq 'value' 这样的形式
     * @param name
     * @param eq   符号，可以写成：=、>、>=、<、<=、eq、neq、gt、egt、lt、elt
     * @param Value
     * @param connect  连接符默认为：and
     * @return
     */
    public QueryWrapper<T> where(String name , String eq , Object Value , String connect)
    {
        HashMap list = new HashMap();
        list.put("name",name);
        list.put("exp" , eq == null ? "=" : eq);
        list.put("value",Value == null ? "" : Value);
        list.put("connect",connect == null ? "and" : connect);

        getOptionArrayList("where").add(list);

        return this;
    }

    /**
     * 设置SQL条件 会自动写成 and field in(inArray) 这样的形式
     * @param field
     * @param inArray
     * @return
     */

    public QueryWrapper<T> whereIn(String field , String inArray)
    {
        String[] arr = inArray.split(",");
        return where(field , "in" , arr);
    }

    /**
     * 设置SQL条件 会自动写成 and field in(inArray1,inArray2) 这样的形式
     * @param field
     * @param inArray
     * @return
     */
    public QueryWrapper<T> whereIn(String field , Object inArray)
    {
        return where(field , "in" , inArray);
    }
    /**
     * 设置SQL条件 会自动写成 and field not in(inArray1) 这样的形式
     * @param field
     * @param inArray
     * @return
     */
    public QueryWrapper<T> whereInNot(String field , Object inArray)
    {
        return where(field , "not in" , inArray);
    }

    /**
     * 设置SQL条件 会自动写成 and field between inArray 这样的形式
     * @param field
     * @param inArray
     * @return
     */
    public QueryWrapper<T> whereBetween(String field , String inArray)
    {
        String[] arr = inArray.split(",");
        return where(field , "between" , arr);
    }
    /**
     * 设置SQL条件 会自动写成 and field between 'start' and 'end' 这样的形式
     * @param field
     * @param start
     * @param end
     * @return
     */
    public QueryWrapper<T> whereBetween(String field , String start , String end)
    {
        return where(field , "between" , "'"+start+"' AND '"+end+"'");
    }

    /**
     * 设置SQL条件 会自动写成 and field not between inArray 这样的形式
     * @param field
     * @param inArray
     * @return
     */
    public QueryWrapper<T> whereBetweenNot(String field , String inArray)
    {
        String[] arr = inArray.split(",");
        return where(field , "not between" , arr);
    }

    /**
     * 设置SQL条件 会自动写成 and field not between 'start' and 'end' 这样的形式
     * @param field
     * @param start
     * @param end
     * @return
     */
    public QueryWrapper<T> whereBetweenNot(String field , String start , String end)
    {
        List<String> data = new ArrayList(2);
        data.add(start);
        data.add(end);
        return where(field , "not between" , data);
    }

    /**
     * 获取connection 连接
     * @return
     */
    public Connection getConn()
    {
        return connectionConfig.getConn();
    }


    /**
     * 执行插入语句
     * @param sql
     * @return
     */
    public int executeInsert(String sql)
    {
        return executeInsert(sql , builder.getBindData());
    }

    /**
     * 执行插入语句
     * @param sql
     * @return
     */
    public int executeInsert(String sql , List bindData)
    {
        PreparedStatement rs=null;
        ResultSet rsKey=null;
        int id = -1;
        try {
            DB.log(sql,bindData);
            Connection conn = this.getConn();
            rs = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            setBindData(rs,bindData);
            rs.executeUpdate();
            rsKey = rs.getGeneratedKeys();
            rsKey.next();
            id = rsKey.getInt(1);

        }catch (SQLException e)
        {
            DB.log(e , sql);
            //e.printStackTrace();
        }finally {
            DB.release(rs , rsKey);
        }
        return id;
    }

    /**
     * 执行更新语句
     * @param sql
     * @return
     */
    public int executeUpdate(String sql)
    {
        return executeUpdate(sql , builder.getBindData());
    }

    /**
     * 执行更新语句
     * @param sql
     * @return
     */
    public int executeUpdate(String sql ,List bindData)
    {
        PreparedStatement rs=null;
        int id = -1;
        try {
            Connection conn = this.getConn();
            rs = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setBindData(rs, bindData);
            id = rs.executeUpdate();
            DB.log(sql,bindData);
        }catch (SQLException e)
        {
            DB.log(e,sql);
        }finally {
            DB.release(rs , null);
        }
        return id;
    }

    /**
     * 快速构建Query
     * @param name
     * @return
     */
    public static<T> QueryWrapper<T> make(String name)
    {
        QueryWrapper query = new QueryWrapper();
        query.setName(name);
        return query;
    }

    /**
     * 获取一页数据，并生成分页代码
     * @param page
     * @return
     */
    public Collect<T> page( Collect<T> page)
    {
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

        this.limit(page.getFirstRow() , page.getPageSize());
        builder.setPage(true);
        List list = select();

        builder.setPage(false);
        page.addAll(list);

        return page;
    }

    /**
     * 获取当前option
     * @return
     */
    public Map getOption() {
        return mOption;
    }

    public List<String> column(String field)
    {
        if(!mOption.containsKey("field"))
        {
            this.field(field);
        }

        QueryMap queryMap = new QueryMap(getName());
        queryMap.mOption.putAll(mOption);
        List<QMap> list = queryMap.select();
        List<String> result = new ArrayList();
        for (QMap map:list){
            result.add(map.getString(field));
        }
        return result;
    }

    public Map column( String field , String key )
    {
        if(!mOption.containsKey("field"))
        {
            this.field(field);
            this.field(key);
        }

        QueryMap queryMap = new QueryMap(getName());
        queryMap.mOption.putAll(mOption);
        List<QMap> list = queryMap.select();

        Map result = new LinkedHashMap();

        for (HashMap map:list){
            result.put(map.get(key),map.get(field));
        }
        return result;
    }

    /**
     * 根据当前条件，获取一列的数据
     * @param field
     * @return
     */
    public List<String> getCol(String field)
    {
        return column(field);
    }

    /**
     * 根据当前条件获取列数据,健对值的关系
     * @param field
     * @param key
     * @return
     */
    public Map getColkey(String field , String key)
    {
        return column(field , key);
    }


}
