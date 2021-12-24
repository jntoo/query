package com.jntoo.db.utils;

import com.alibaba.fastjson.JSON;
import com.jntoo.db.Configuration;
import com.jntoo.db.annotation.*;
import com.jntoo.db.callback.HasQueryCallback;
import com.jntoo.db.has.HasManyQuery;
import com.jntoo.db.has.HasOneQuery;
import com.jntoo.db.has.HasQuery;
import com.jntoo.db.model.FieldInfoModel;
import com.jntoo.db.model.TableModel;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableManageUtils {
    static public Map<Class<?> , TableModel> tableModelMap;
    static {
        tableModelMap = new ConcurrentHashMap();
    }

    public static TableModel getTable(Class<?> table)
    {
        TableModel model = tableModelMap.get(table);
        if (  model == null ) {
            model = createTable(table);
            tableModelMap.put(table , model);
        }
        return model;
    }

    private static synchronized TableModel createTable(Class<?> table )
    {
        // 处理表信息
        TableModel tableModel = new TableModel();
        Table annTable = table.getAnnotation(Table.class);
        tableModel.entity = table;
        if(annTable != null)
        {
            if( !StringUtil.isNullOrEmpty(annTable.value()) )
            {
                tableModel.setName(annTable.value());
            }else{
                tableModel.setName(StringUtil.camelToUnderline(table.getSimpleName(),1));
            }
            if(annTable.sysPrefix()){
                tableModel.setPrefix(Configuration.getPrefix());
            }else{
                // 不使用系统前缀
                tableModel.setPrefix("");
            }
            if( !StringUtil.isNullOrEmpty(annTable.prefix()) )
            {
                tableModel.setPrefix(annTable.prefix());
            }
        }else{
            tableModel.setName(StringUtil.camelToUnderline(table.getSimpleName(),1));
            tableModel.setPrefix(Configuration.getPrefix());
        }
        handlerTableField(tableModel , table);
        return tableModel;
    }

    private static void handlerTableField( TableModel tableModel , Class<?> table)
    {
        Field[] fields = table.getDeclaredFields();
        Class<ResultSet> resultSetClass = ResultSet.class;

        for (Field field : fields) {
            if((field.getModifiers() & (Modifier.STATIC | Modifier.FINAL)) > 0){
                continue;
            }
            String fieldName     = field.getName();
            if(tableModel.getFieldInfo(fieldName) != null){
                continue;
            }
            Fields annotation = field.getAnnotation(Fields.class);

            HasOne hasOne = field.getAnnotation(HasOne.class);
            HasMany hasMany = field.getAnnotation(HasMany.class);

            FieldInfoModel model = new FieldInfoModel();
            Class<? extends FieldInfoModel> modelClass = model.getClass();

            String annotName     = annotation != null ? annotation.value() : "";
            String zhenshiName   = StringUtil.isNullOrEmpty(annotName) ? fieldName : annotName;

            if(annotation!=null){
                if(annotation.autoInsertTime())
                {
                    tableModel.autoInsertTimeField.add(fieldName);
                }
                if(annotation.autoUpdateTime())
                {
                    tableModel.autoUpdateTimeField.add(fieldName);
                }
                if(annotation.type() == FieldType.PK || annotation.type() == FieldType.PK_AUTO)
                {
                    tableModel.pk = zhenshiName;
                }
            }

            Class<?> type = field.getType();
            try {
                model.setClassPojo(table);
                setFieldValue(modelClass.getDeclaredField("name") , model , zhenshiName);
                setFieldValue(modelClass.getDeclaredField("field") , model , field);
                setFieldValue(modelClass.getDeclaredField("annField") , model , annotation);

                Method method = table.getMethod("set" + StringUtil.firstCharUpper(fieldName), type);

                setFieldValue(modelClass.getDeclaredField("setMethod") , model , method);
                if(type == String.class)
                {
                    setFieldValue(modelClass.getDeclaredField("getMethod") , model , resultSetClass.getMethod("getString" , String.class));
                }else if (type == Integer.class || type == int.class){
                    setFieldValue(modelClass.getDeclaredField("getMethod") , model , resultSetClass.getMethod("getInt", String.class));
                }else if (type == Long.class || type == long.class){
                    setFieldValue(modelClass.getDeclaredField("getMethod") , model , resultSetClass.getMethod("getLong", String.class));
                }else if (type == Double.class || type == double.class){
                    setFieldValue(modelClass.getDeclaredField("getMethod") , model , resultSetClass.getMethod("getDouble", String.class));
                }else if (type == Float.class || type == float.class){
                    setFieldValue(modelClass.getDeclaredField("getMethod") , model , resultSetClass.getMethod("getFloat", String.class));
                }else if (type == Boolean.class  || type == boolean.class){
                    setFieldValue(modelClass.getDeclaredField("getMethod") , model , resultSetClass.getMethod("getBoolean", String.class));
                }else if(Date.class.isAssignableFrom(type)
                        || Time.class.isAssignableFrom(type)
                        || Blob.class.isAssignableFrom(type)
                        || URL.class.isAssignableFrom(type)
                ){
                    // 日期格式
                    setFieldValue(modelClass.getDeclaredField("getMethod") , model , resultSetClass.getMethod("get"+type.getSimpleName(), String.class));
                }else if(type == byte[].class){
                    setFieldValue(modelClass.getDeclaredField("getMethod") , model , resultSetClass.getMethod("getBytes", String.class));
                }else if(type == byte.class){
                    setFieldValue(modelClass.getDeclaredField("getMethod") , model , resultSetClass.getMethod("getByte", String.class));
                }else if(type == BigDecimal.class){
                    setFieldValue(modelClass.getDeclaredField("getMethod") , model , resultSetClass.getMethod("getBigDecimal", String.class));
                }else{
                    if(annotation != null && annotation.type() == FieldType.JSON){
                        // json 数据格式
                        setFieldValue(modelClass.getDeclaredField("getMethod") , model , resultSetClass.getMethod("getString" , String.class));
                    }else{
                        //throw new RuntimeException("没有找到该类型： "+type);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            if(annotation != null && !StringUtil.isNullOrEmpty(annotation.autoUpdate()))
            {
                try{
                    String key = "auto"+StringUtil.firstCharUpper(fieldName)+"Update";
                    Method method = table.getMethod(key , Map.class);
                    setFieldValue(modelClass.getDeclaredField("autoMethodUpdate") , model , method);
                    tableModel.autoUpdateField.add(fieldName);
                }catch (Exception e){
                    try {
                        setFieldValue(modelClass.getDeclaredField("autoMethodUpdateString") , model , annotation.autoUpdate());
                    } catch (IllegalAccessException illegalAccessException) {
                        illegalAccessException.printStackTrace();
                    } catch (NoSuchFieldException noSuchFieldException) {
                        noSuchFieldException.printStackTrace();
                    }
                }
            }

            if(annotation != null && !StringUtil.isNullOrEmpty(annotation.autoInsert()))
            {
                try{
                    Method method = table.getMethod("auto"+StringUtil.firstCharUpper(field.getName())+"Insert" , Map.class);
                    setFieldValue(modelClass.getDeclaredField("autoMethodInsert") , model , method);
                    tableModel.autoInserField.add(fieldName);
                } catch (Exception e) {
                    try {
                        setFieldValue(modelClass.getDeclaredField("autoMethodInsertString") , model , annotation.autoInsert());
                    } catch (IllegalAccessException illegalAccessException) {
                        illegalAccessException.printStackTrace();
                    } catch (NoSuchFieldException noSuchFieldException) {
                        noSuchFieldException.printStackTrace();
                    }
                }
            }

            if( hasOne != null || hasMany != null)
            {
                HasModel hasModel;
                if(hasOne!=null)
                {
                    hasModel = new HasModel(hasOne);
                }else{
                    hasModel = new HasModel(hasMany);
                }

                HasQuery oneQuery = hasModel.hasQuery;
                if (hasModel.target != void.class)
                {
                    oneQuery.setTarget(hasModel.target);
                }else{
                    if(List.class.isAssignableFrom(type)){
                        ParameterizedType parameterizedType = (ParameterizedType)field.getGenericType();
                        Type[] types = parameterizedType.getActualTypeArguments();
                        oneQuery.setTarget((Class<?>) types[0]);
                    }else{
                        oneQuery.setTarget(type);
                    }
                }

                if(StringUtil.isNullOrEmpty(hasModel.foreignKey)){
                    oneQuery.setForeignKey(tableModel.getPk());
                }else{
                    oneQuery.setForeignKey(hasModel.foreignKey);
                }

                if(StringUtil.isNullOrEmpty(hasModel.localKey))
                {
                    oneQuery.setLocalKey("id");
                }else{
                    oneQuery.setLocalKey(hasModel.localKey);
                }

                oneQuery.setField(field.getName());
                if(hasModel.where.length > 0)
                {
                    for (String s : hasModel.where) {
                        oneQuery.where(s);
                    }
                }
                if(hasModel.order.length > 0)
                {
                    for (String s : hasModel.order) {
                        oneQuery.order(s);
                    }
                }

                if(hasModel.field.length > 0)
                {
                    for (String s : hasModel.field) {
                        oneQuery.field(s);
                    }
                }

                if(hasModel.callback != void.class && HasQueryCallback.class.isAssignableFrom(hasModel.callback))
                {
                    try {
                        Object o = hasModel.callback.newInstance();
                        Method run = HasQueryCallback.class.getMethod("run", HasQuery.class);
                        run.invoke(o , oneQuery);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                tableModel.addHasQuery(oneQuery);
            }
            tableModel.setFieldInfo(fieldName , model);
        }

        Class<?> superclass = table.getSuperclass();
        if( superclass != null && superclass != void.class)
        {
            handlerTableField(tableModel , superclass);
        }
    }

    public static void setFieldValue(Field field , Object data , Object value) throws IllegalAccessException
    {
        String name = field.getName();
        try {
            Method method = data.getClass().getMethod("set"+StringUtil.firstCharUpper(name) , value.getClass());
            method.invoke(data , value);
        } catch (Exception e) {
            boolean isAcc = field.isAccessible();
            if (!isAcc) field.setAccessible(true);
            field.set(data,value);
            if (!isAcc) field.setAccessible(false);
        }
    }

    public static Object getFieldValue(Field field , Object data ) throws IllegalAccessException
    {
        String name = field.getName();
        Object res = null;
        try {
            Method method = data.getClass().getMethod("get"+StringUtil.firstCharUpper(name) );
            res = method.invoke(data);
        } catch (Exception e) {
            field.setAccessible(true);
            res = field.get(data);
        }
        return res;

    }

}
