package com.jntoo.db.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableModel {
    // 表名称
    public String name;
    // 表前缀
    public String prefix;
    // 主键字段
    public String pk = "id";
    //
    public Class<?> entity;

    public Map<String,FieldInfoModel> fieldInfo = new HashMap();
    public List<String> autoInsertTimeField = new ArrayList();
    public List<String> autoInserField = new ArrayList();
    public List<String> autoUpdateTimeField = new ArrayList();
    public List<String> autoUpdateField = new ArrayList();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }


    public FieldInfoModel getFieldInfo(String field) {
        return fieldInfo.get(field);
    }

    public void setFieldInfo(String field ,  FieldInfoModel fieldInfo) {
        this.fieldInfo.put(field , fieldInfo);
    }

    public Class<?> getEntity() {
        return entity;
    }
    public void setEntity(Class<?> entity) {
        this.entity = entity;
    }
}
