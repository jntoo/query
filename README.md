# 数据库链式操作类
### 参数设置
```
配置文件：jntoodb.properties
参数：
jntoo.db.debug=true  是否输出调试信息
jntoo.db.connection  设置获取数据库链接类名称继承自 ConnectionConfig 类,必须设置
jntoo.db.prefix      设置表前缀
```

## DB 静态类 创建QueryWrapper 类
```
DB.name(“表名称”)       创建 QueryWrapper 类，该类操作返回得数据为：QMap 类型数据
DB.name(Pojo类.class)  设置实体类，按实体类返回相关数据
DB.name(Pojo 实例)     设置实例，相关数据在查询时会以非null 得值写入查询条件，使用insert、update 插入即可插入
```

## CURD 操作
### `Select`查询
#### where 操作 `(可写多个条件)`
###### where(自定义条件) 不限制where 条件写法 等同于SQL语句： where 自定义条件 如下：

````
DB.name("table").where("1=1 AND field='12'").select();
select * from table WHERE 1=1 AND field='12'
````

#### where(String field , Object value)  等于条件 如：
````
DB.name("table").where("field",12).select();
select * from table where field=?  条件 ?=12  自动写入绑定
````
#### where(String field ,String exp, Object value)  等于条件 如：
````
field  字段名
exp    条件符号  可写 =、!=、>、>=、<、<=、in、not in、between、not between、like、not like
       其中 =、!=、 > 、>= 、< 、<= 可以使用
          eq、neq、gt、egt、lt、elt 替换
value  条件值

DB.name("table").where("field1" , "like" ,"%data%").where("field2","eq",12).select();
select * from table where field1 like ? field2=?  条件 ?1='%data%'  ?2=12  自动写入绑定
````

#### where 便捷方法
###### `whereIn`  `whereInNot` `whereLike` `whereLikeNot` `whereBetween` `whereBetweenNot`

#### order 排序 `(可写多个条件)`
#####  order(String order) 设置排序字段
````
DB.name("table").where("field",12).order("id desc").select();
select * from table where field=? order by id desc 条件 ?=12  自动写入绑定
````

#####  order(String field ,String sort) 设置排序字段

````
DB.name("table").where("field",12).order("id","desc").select();
select * from table where field=? order by id desc 条件 ?=12  自动写入绑定
````

#####  orderDesc(String field) 设置排序字段

````
DB.name("table").where("field",12).orderDesc("id").select();
select * from table where field=? order by id desc 条件 ?=12  自动写入绑定
````

#####  orderAsc(String field) 设置排序字段

````
DB.name("table").where("field",12).orderAsc("id").select();
select * from table where field=? order by id desc 条件 ?=12  自动写入绑定
````

