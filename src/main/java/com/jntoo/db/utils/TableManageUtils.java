package com.jntoo.db.utils;

import com.alibaba.fastjson.JSON;
import com.jntoo.db.Configuration;
import com.jntoo.db.annotation.FieldType;
import com.jntoo.db.annotation.Fields;
import com.jntoo.db.annotation.Table;
import com.jntoo.db.model.FieldInfoModel;
import com.jntoo.db.model.TableModel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
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

    private static synchronized void handlerTableField( TableModel tableModel , Class<?> table)
    {
        Field[] fields = table.getDeclaredFields();

        Class<ResultSet> resultSetClass = ResultSet.class;

        for (Field field : fields) {
            Fields annotation = field.getAnnotation(Fields.class);
            FieldInfoModel model = new FieldInfoModel();
            Class<? extends FieldInfoModel> modelClass = model.getClass();

            String fieldName     = field.getName();
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
                        throw new RuntimeException("没有找到该类型： "+type);
                    }
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

            if(annotation != null && !StringUtil.isNullOrEmpty(annotation.autoUpdate()))
            {
                try{
                    Method method = tableModel.getClass().getMethod("auto"+StringUtil.firstCharUpper(fieldName)+"Update" , Map.class);
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
                    Method method = tableModel.getClass().getMethod("auto"+StringUtil.firstCharUpper(field.getName())+"Insert" , Map.class);
                    setFieldValue(modelClass.getDeclaredField("autoMethodInsert") , model , method);
                    tableModel.autoInserField.add(fieldName);
                }catch (Exception e){
                    try {
                        setFieldValue(modelClass.getDeclaredField("autoMethodInsertString") , model , annotation.autoInsert());
                    } catch (IllegalAccessException illegalAccessException) {
                        illegalAccessException.printStackTrace();
                    } catch (NoSuchFieldException noSuchFieldException) {
                        noSuchFieldException.printStackTrace();
                    }
                }
            }
            tableModel.setFieldInfo(fieldName , model);

        }
    }


    public static void setFieldValue(Field field , Object data , Object value) throws IllegalAccessException
    {
        boolean isAcc = field.isAccessible();
        if (!isAcc) field.setAccessible(true);

        field.set(data,value);

        if (!isAcc) field.setAccessible(false);
    }

}
