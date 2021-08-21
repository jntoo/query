# 数据库链式操作类、目前只支持Mysql、SqlServer、后续会补上其他
### 参数设置
```
配置文件：jntoodb.properties
参数：
jntoo.db.debug=true  是否输出调试信息
jntoo.db.connection  设置获取数据库链接类名称继承自 ConnectionConfig 类,必须设置
jntoo.db.prefix      设置表前缀·设置后，QueryWrapper 会自动设置表得前缀
```

温馨提示、解释下方得sql 语句为编译后得结果展示

## Pojo实体类
### `Table`  注解
````
value      设置表名称、不填使用Class 类名称设计表，采用下划线方式
prefix     单独设置表前缀、不设置则使用配置得表前缀，prefix优先级高于系统配置
sysPrefix  是否使用配置得表前缀、默认为true
````

### `Fields`  字段注解
````
value      字段名称、不设置则采用属性名称
type       设置字段类型、默认为普通、FieldType 目前只支持、默认、主键、自增主键、后期考虑增加JSON数组格式
````




## DB 静态类 创建QueryWrapper 实例
```
DB.name(“表名称”)       创建 QueryMap 类，该类操作返回得数据为：QMap 类型数据
DB.name(Pojo类.class)  设置实体类，按实体类返回相关数据
DB.name(Pojo 实例)     设置实例，相关数据在查询时会以非null 得值写入查询条件，使用insert、update 插入即可插入
```

## CURD 操作

### `Update` 更新
#### DB.name(“表名称”).update(Map map) 数据更新方法，非空才会进行更新，空值不更新数据

````java
Map map = new HashMap();

map.put("id" , 1);
map.put("field1" , 1);
map.put("field2" , 2);
map.put("field3" , 3);
Db.name("table").update(map);

````

#### DB.name(Pojo.class).update(pojo) Pojo.class 方式更新方法
````
// Pojo.class
Pojo pojo = new Pojo();
pojo.id = 1;
pojo.field1 = 1;
pojo.field2 = 2;
pojo.field3 = 3;
DB.name(Pojo.class).update(pojo); //数据更新方法默认按 主键id 进行条件更新
````

#### DB.name(pojo).update() 实例数据更新方法，
````java
// Pojo 实例
Pojo pojo = new Pojo();
pojo.id = 1;
pojo.field1 = 1;
pojo.field2 = 2;
pojo.field3 = 3;
DB.name(pojo).update(); //数据更新方法默认按 主键id 进行条件更新
````


##### ······ **温馨提示：更新操作可以使用where 设置条件进行更新**


### `Insert` 插入操作
#### DB.name(“表名称”).insert(Map map)

````java
Map map = new HashMap();

map.put("id" , 1);
map.put("field1" , 1);
map.put("field2" , 2);
map.put("field3" , 3);
Db.name("table").insert(map);

````

#### DB.name(Pojo.class).insert(pojo) Pojo.class
````
// Pojo.class
Pojo pojo = new Pojo();
pojo.id = 1;
pojo.field1 = 1;
pojo.field2 = 2;
pojo.field3 = 3;
DB.name(Pojo.class).insert(pojo);
````

#### DB.name(pojo).insert() 实例数据更新方法，
````java
// Pojo 实例
Pojo pojo = new Pojo();
pojo.id = 1;
pojo.field1 = 1;
pojo.field2 = 2;
pojo.field3 = 3;
DB.name(pojo).insert();
````

### `Delete` 删除数据
DB.name("table").delete(int id)  根据主键 删除某行数据
````
DB.name("table").delete(1);   

// delete from table where id=?   ?=1
````

DB.name("table").delete(List id)  根据列表主键 删除多行数据
````
List list = new ArrayList();
list.add(1);
list.add(2);
list.add(3);
DB.name("table").delete(list);   

// delete from table where id in (?,?,?)   ?1=1  ?2=2  ?3=3
````

DB.name("table").where(条件1).where(条件2).delete()  根据自定义条件删除多行数据
````
DB.name("table").where("field" , 1).where("field2" , 2).delete();   

// delete from table where id field=? AND field2=?   ?1=1  ?2=2
````




### `Select`查询多行
#### where 操作 `(可写多个条件)`
#### where(自定义条件) 不限制where 条件写法 等同于SQL语句： where 自定义条件 如下：

````
DB.name("table").where("1=1 AND field='12'").select();
// select * from table WHERE 1=1 AND field='12'
````

#### where(String field , Object value)  等于条件 如：
````
DB.name("table").where("field",12).select();
// select * from table where field=?  条件 ?=12  自动写入绑定
````
#### where(String field ,String exp, Object value)  等于条件 如：
````
field  字段名
exp    条件符号  可写 =、!=、>、>=、<、<=、in、not in、between、not between、like、not like
       其中 =、!=、 > 、>= 、< 、<= 可以使用
          eq、neq、gt、egt、lt、elt 替换
value  条件值

DB.name("table").where("field1" , "like" ,"%data%").where("field2","eq",12).select();
// select * from table where field1 like ? field2=?  条件 ?1='%data%'  ?2=12  自动写入绑定
````

#### where 便捷方法
##### `whereIn`  `whereInNot` `whereLike` `whereLikeNot` `whereBetween` `whereBetweenNot`

#### order 排序 `(可写多个条件)`
#####·····  order(String order) 设置排序字段
````
DB.name("table").where("field",12).order("id desc").select();
// select * from table where field=? order by id desc 条件 ?=12  自动写入绑定
````

#####·····  order(String field ,String sort) 设置排序字段

````
DB.name("table").where("field",12).order("id","desc").select();
// select * from table where field=? order by id desc 条件 ?=12  自动写入绑定
````

#####·····  orderDesc(String field) 设置排序字段

````
DB.name("table").where("field",12).orderDesc("id").select();
// select * from table where field=? order by id desc 条件 ?=12  自动写入绑定
````

#####·····  orderAsc(String field) 设置排序字段

````
DB.name("table").where("field",12).orderAsc("id").select();
// select * from table where field=? order by id Asc 条件 ?=12  自动写入绑定
````

#### limit 设置获取条数 兼容（mysql sqlserver）
#####·····  limit(long size) 设置获取条数
````
DB.name("table").where("field",12).orderAsc("id").limit(10).select();
// select * from table where field=? order by id Asc LIMIT ? 条件 ?1=12 ?2=10   自动写入绑定
````

#####·····  limit(long offset , long size) 设置从某位置开始获取多少条数据
````
DB.name("table").where("field",12).orderAsc("id").limit(0,20).select();
// select * from table where field=? order by id Asc LIMIT ?,? 条件 ?1=12 ?2=0 ?3=20   自动写入绑定
````

#### join 数据库join链接操作
#####····· joinLeft(String table , String condtion) 设置左链接
#####····· joinRight(String table , String condtion) 设置右链接
#####····· joinInner(String table , String condtion) 设置全链接
````java
DB.name("table1").alias("t1")
.joinLeft("table2 t2" , "t1.id=t2.id")
.field("t1.*")
.field("t2.name")
.where("t1.data" , 1)
.limit(20)
.select();
````
````mysql
select t1.*,t2.name 
FROM table1 as t1 
LEFT JOIN table2 t2 ON t1.id=t2.id 
where t1.data=?
limit 20

?=1
````

