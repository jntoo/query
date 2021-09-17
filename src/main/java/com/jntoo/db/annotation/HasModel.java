package com.jntoo.db.annotation;

import com.jntoo.db.callback.HasQueryCallback;
import com.jntoo.db.has.HasManyQuery;
import com.jntoo.db.has.HasOneQuery;
import com.jntoo.db.has.HasQuery;

public class HasModel {
    public String foreignKey; // 外键关联字段
    public  String localKey;   // 目标表的关联字段
    public Class<?> target;  // 所属类型，不填写则默认字段上的类型
    public Class<?> callback;

    public String[] where;
    public String[] field;
    public String[] order;
    public HasQuery hasQuery;

    public HasModel()
    {

    }

    public HasModel(HasOne hasOne) {
        foreignKey = hasOne.foreignKey(); // 外键关联字段
        localKey = hasOne.localKey();   // 目标表的关联字段
        target = hasOne.target();  // 所属类型，不填写则默认字段上的类型
        where = hasOne.where();
        field = hasOne.field();
        order = hasOne.order();
        callback = hasOne.callback();
        hasQuery = new HasOneQuery();

    }

    public HasModel(HasMany hasMany)
    {
        foreignKey = hasMany.foreignKey(); // 外键关联字段
        localKey = hasMany.localKey();   // 目标表的关联字段
        target = hasMany.target();  // 所属类型，不填写则默认字段上的类型
        where = hasMany.where();
        field = hasMany.field();
        order = hasMany.order();
        callback = hasMany.callback();
        hasQuery = new HasManyQuery();
    }
}
